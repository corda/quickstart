package org.r3.kyc.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.r3.kyc.contracts.CCDCommands;
import org.r3.kyc.contracts.CCDContract;
import org.r3.kyc.entity.Address;
import org.r3.kyc.states.CCDState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;

import static net.corda.core.contracts.ContractsDSL.requireThat;


// ******************
// * Initiator flow *
// ******************

public class CCDIssueFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private static final Logger logger = LoggerFactory.getLogger(Initiator.class);


        private final Party otherParty;
        private final String companyName;
        private final UniqueIdentifier gleifId;
        private final int qal;

        private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating the transaction");
        private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying the transaction");
        private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing the transaction");
        private final ProgressTracker.Step GATHERING_SIGNATURES = new ProgressTracker.Step("Gathering the transaction");
        private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Finalising the transaction");
            public Initiator(Party otherParty, String companyName, UniqueIdentifier gleifId, int qal) {
                this.otherParty = otherParty;
                this.companyName = companyName;
                this.gleifId = gleifId;
                this.qal = qal;
                System.out.println("Initiated CCDIssueFlow");
            }


        private final ProgressTracker progressTracker = new ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGNATURES,
                FINALISING_TRANSACTION
        );
        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            // Initiator flow logic goes here.
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            // This is the issuer, the gateway in this case
            Party me = getOurIdentity();
            logger.info("Running as "+me.getName().toString());

            // Constructs
            Address address = new Address("1 test street", "Town");
            CCDState ccd = new CCDState(otherParty,
                    null,
                    address,
                    new UniqueIdentifier(),
                    otherParty,
                    gleifId,
                    companyName,
                    qal);

            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            final Command<CCDCommands.Issue> txCommand = new Command<>(
                    new CCDCommands.Issue(),
                    Arrays.asList(ccd.getIssuer().getOwningKey(), ccd.getOwner().getOwningKey()));

            // Now make the transactionbuilder construction
            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(ccd, CCDContract.ID)
                    .addCommand(txCommand);


            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            // Make sure that verification passes
            txBuilder.verify(getServiceHub());

            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            FlowSession otherPartySession = initiateFlow(otherParty);

//            try {
                progressTracker.setCurrentStep(GATHERING_SIGNATURES);
                final SignedTransaction fullySignedTx = subFlow(
                        new CollectSignaturesFlow(partSignedTx, Arrays.asList(otherPartySession))
                );
                progressTracker.setCurrentStep(FINALISING_TRANSACTION);
                return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
//            } catch (Exception e) {
//                throw new FlowException("Problem");
//            }
        }


    }
    @InitiatedBy(Initiator.class)
    public static class Acceptor extends FlowLogic<SignedTransaction> {
        private final FlowSession otherPartySession;
        public Acceptor(FlowSession otherPartySession) {this.otherPartySession = otherPartySession; }


        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        CCDState output = (CCDState) stx.getTx().getOutputs().get(0).getData();
                        require.using ("This must be a CCD transaction", output instanceof CCDState);
                        require.using ("Quality should be above 5", output.getQal() > 5);
                        return null;

                    });
                }

            }
            final SignTxFlow signTxFlow = new SignTxFlow(otherPartySession, SignTransactionFlow.Companion.tracker());
            final SecureHash txId = subFlow(signTxFlow).getId();
            return subFlow(new ReceiveFinalityFlow(otherPartySession, txId));

        }
    }

}

