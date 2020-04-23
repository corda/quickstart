package org.r3.kyc.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.r3.kyc.contracts.CCDCommands;
import org.r3.kyc.contracts.CCDContract;
import org.r3.kyc.entity.Address;
import org.r3.kyc.states.CCDState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CCDAmendFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final static Logger logger = LoggerFactory.getLogger(Initiator.class);


        private final UniqueIdentifier stateLinearId;
        private final Party owner;
        private final UniqueIdentifier gleifId; // Legal entity identifier
        private final String company;
        private final int qal;

        private final ProgressTracker.Step FETCHING_PREVIOUS_STATE = new ProgressTracker.Step("Fetching previous state");
        private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating the transaction");
        private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying the transaction");
        private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing the transaction");
        private final ProgressTracker.Step GATHERING_SIGNATURES = new ProgressTracker.Step("Gathering the transaction");
        private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Finalising the transaction");
        private final ProgressTracker.Step RESPONDER_SIGN = new ProgressTracker.Step("Responder sign");

        private final ProgressTracker progressTracker = new ProgressTracker(
                FETCHING_PREVIOUS_STATE,
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGNATURES,
                FINALISING_TRANSACTION,
                RESPONDER_SIGN
        );
        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        public Initiator(UniqueIdentifier stateLinearId, Party owner, UniqueIdentifier gleifId, String company, int qal) {
            this.stateLinearId = stateLinearId;
            this.owner = owner;
            this.gleifId = gleifId;
            this.company = company;
            this.qal = qal;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // 1 Retrieve the state from the vault
            List<UUID> listOfLinearIds = new ArrayList<>();
            listOfLinearIds.add(stateLinearId.getId());
            progressTracker.setCurrentStep(FETCHING_PREVIOUS_STATE);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, listOfLinearIds);

            // 2 Get the reference to the state that we are using as input
            Vault.Page results = getServiceHub().getVaultService().queryBy(CCDState.class,queryCriteria);

            logger.info("Vault size = "+results.getStates().size());
            if (results.getStates().size()==0) {
                throw new FlowException("The vault is empty!");
            }
            StateAndRef inputStateAndRefToAmend = (StateAndRef) results.getStates().get(0);
            CCDState inputStateToAmend = (CCDState) inputStateAndRefToAmend.getState().getData();

            // 3 Start to think about what we need in terms of components for a transaction
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            progressTracker.setCurrentStep(GENERATING_TRANSACTION);

            TransactionBuilder tb = new TransactionBuilder(notary);

            // 4 Now add a command to the transaction

            Command<CCDCommands.Amend> command = new Command<>(
                    new CCDCommands.Amend(),
                    Arrays.asList( owner.getOwningKey())
            );

            // Add to transaction builder
            tb.addCommand(command);
            // Now the input and output states
            tb.addInputState(inputStateAndRefToAmend);

            Address address = new Address("1 test street", "Town");
            Party issuer = ((CCDState) inputStateAndRefToAmend.getState().getData()).getIssuer();
            CCDState ccd = new CCDState(
                    issuer,
                    null,
                    address,
                    stateLinearId,
                    getOurIdentity(),
                    gleifId,
                    ((CCDState) inputStateAndRefToAmend.getState().getData()).getCompany(),
                    qal);

            // Add output state
            tb.addOutputState(ccd, CCDContract.ID);

            // Test that the flow is being called by the owner
            if (!inputStateToAmend.getOwner().getOwningKey().equals(getOurIdentity().getOwningKey())) {
                throw new IllegalArgumentException("This flow must be run by the current owner.");
            }

            // Verify / sign it
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            logger.info("Verifying the transaction");
            logger.info("Input state size = "+tb.inputStates().size());
            tb.verify(getServiceHub());
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(tb);


            // Lets collect all signatures but not from ourself !
            progressTracker.setCurrentStep(GATHERING_SIGNATURES);
            List<FlowSession> sessions = new ArrayList<>();
            for (AbstractParty participant : inputStateToAmend.getParticipants()) {
                Party partyToInitiateFlow = (Party) participant;
                if (!partyToInitiateFlow.getOwningKey().equals(getOurIdentity().getOwningKey())) {
                    sessions.add(initiateFlow(partyToInitiateFlow));
                }
            }

            SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, sessions));

            // Finalise send to notaruy, checks, and back stores in vault
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            return subFlow(new FinalityFlow(fullySignedTransaction, sessions));



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
                        require.using ("This should be a single command transaction ", stx.getTx().getCommands().size()==1);

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
