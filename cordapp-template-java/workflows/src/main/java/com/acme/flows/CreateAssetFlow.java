package com.acme.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.acme.contracts.AssetContract;
import com.acme.states.AssetState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Collections;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class CreateAssetFlow extends FlowLogic<SignedTransaction> {


    private static final ProgressTracker.Step ID_OTHER_NODES = new ProgressTracker.Step("Choosing a notary for the transaction");
    private static final ProgressTracker.Step VERIFYING_TX = new ProgressTracker.Step("Verifying a transaction");
    private static final ProgressTracker.Step SIGN_TX = new ProgressTracker.Step("Signing a transaction");
    private static final ProgressTracker.Step FINALISATION = new ProgressTracker.Step("Finalising a transaction.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.tracker();
        }
    };
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private final ProgressTracker progressTracker = new ProgressTracker(
            ID_OTHER_NODES,

            VERIFYING_TX ,
            SIGN_TX,
            FINALISATION
    );

    private final String title;
    private final String description;
    private final String imageURL;

    /**
     * Constructor to initialise flow parameters received from rpc.
     *
     * @param title of the asset to be issued on ledger
     * @param description of the asset to be issued in ledger
     * @param imageURL is a url of an image of the asset
     */
    public CreateAssetFlow(String title, String description, String imageURL) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
    }
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Initiator flow logic goes here.
        progressTracker.setCurrentStep(ID_OTHER_NODES);
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Next we offer up an output state
        // Create the output state and assign ownership to the originating party (us)
        AssetState output = new AssetState(new UniqueIdentifier(), title, description, imageURL,
                getOurIdentity());

        // Build the transaction, add the output state and the command to the transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(output)
                .addCommand(new AssetContract.Commands.Create(),
                        getOurIdentity().getOwningKey()); // Required Signers

        // Verify the transaction
        progressTracker.setCurrentStep(VERIFYING_TX );
        transactionBuilder.verify(getServiceHub());


        // Sign the transaction
        progressTracker.setCurrentStep(SIGN_TX );
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // Notarise the transaction and record the state in the ledger.
        progressTracker.setCurrentStep(FINALISATION);
        return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));

    }
}
