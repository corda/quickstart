package org.r3.flow;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.checkerframework.common.aliasing.qual.Unique;
import org.r3.contract.AuctionContract;
import org.r3.state.AuctionState;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateAuctionFlow {


    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final ProgressTracker progressTracker = new ProgressTracker();

        private final Amount<Currency> basePrice;
        private final UUID auctionItem;
        private final LocalDateTime bidDeadLine;

        public Initiator(Amount<Currency> basePrice, UUID auctionItem, LocalDateTime bidDeadLine) {
            this.basePrice = basePrice;
            this.auctionItem = auctionItem;
            this.bidDeadLine = bidDeadLine;
        }

        public ProgressTracker getProgressTracker() { return progressTracker;}

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            Party auctioneer = getOurIdentity();
            List<Party> bidders = getServiceHub().getNetworkMapCache().getAllNodes().stream()
                    .map(nodeInfo -> nodeInfo.getLegalIdentities().get(0))
                    .collect(Collectors.toList());
            // Now we have everyone, remove ourselves and the notary as we are not involved in bidding
            bidders.remove(auctioneer);
            bidders.remove(notary);

            AuctionState auctionState = new AuctionState(
                    new LinearPointer<>(new UniqueIdentifier(null, auctionItem), LinearState.class),
                    UUID.randomUUID(),
                    basePrice,
                    null,
                    null,
                    bidDeadLine.atZone(ZoneId.systemDefault()).toInstant(),
                    null,
                    true,
                    auctioneer,
                    bidders,
                    null);

            // Build the tx
            TransactionBuilder builder = new TransactionBuilder(notary)
                    .addOutputState(auctionState)
                    .addCommand(new AuctionContract.Commands.Create(), auctioneer.getOwningKey());

            // Verify it
            builder.verify(getServiceHub());
            // Sign it
            SignedTransaction selfSignedTransaction = getServiceHub().signInitialTransaction(builder);

            // Notarise tx and store in vault
            List<FlowSession> bidderSessions = new ArrayList<>();
            for(Party bidder: bidders) {
                bidderSessions.add(initiateFlow(bidder));
            }
            return subFlow(new FinalityFlow(selfSignedTransaction, bidderSessions));
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends FlowLogic<SignedTransaction> {
        private FlowSession counterpartySession;
        public Responder(FlowSession counterpartySession) { this.counterpartySession = counterpartySession;}

        @Suspendable
        public SignedTransaction call() throws FlowException {

            return subFlow(new ReceiveFinalityFlow(counterpartySession));
        }
    }

}
