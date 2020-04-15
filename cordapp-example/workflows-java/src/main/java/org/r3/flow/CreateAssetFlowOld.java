package org.r3.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.example.flow.ExampleFlow;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.r3.contract.AssetContract;
import org.r3.state.AssetState;

import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class CreateAssetFlowOld extends FlowLogic<SignedTransaction> {

    private final ProgressTracker.Step FETCHING_NOTARY= new ProgressTracker.Step("Fetching notary");
    private final ProgressTracker.Step FINALISING= new ProgressTracker.Step("Finalising");

    private final ProgressTracker progressTracker = new ProgressTracker(
            FETCHING_NOTARY,
            FINALISING
    );

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private String title;
    private  String description;
    private String imageURL;




    @Override

    public SignedTransaction call() throws FlowException {

        //progressTracker.setCurrentStep(FETCHING_NOTARY);

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        AssetState output = new AssetState(title, description,imageURL, new UniqueIdentifier(),
                getOurIdentity());

        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(output)
                .addCommand(new AssetContract.Commands.Create(), getOurIdentity().getOwningKey()); // Required signers
        transactionBuilder.verify(getServiceHub());

        // Sign it
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        //progressTracker.setCurrentStep(FINALISING);



        return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
    }



}


