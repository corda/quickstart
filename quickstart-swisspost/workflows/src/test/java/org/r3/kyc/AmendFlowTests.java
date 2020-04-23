package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.r3.kyc.flows.CCDAmendFlow;
import org.r3.kyc.flows.CCDIssueFlow;
import org.r3.kyc.states.CCDState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class AmendFlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
        TestCordapp.findCordapp("org.r3.kyc.contracts"),
        TestCordapp.findCordapp("org.r3.kyc.flows")
    )));

    private final static Logger logger = LoggerFactory.getLogger(AmendFlowTests.class);

    private final StartedMockNode gateway = network.createNode();
    private final StartedMockNode rou = network.createNode();

    // Use the corda-training piece to bolt this on

    public AmendFlowTests() {
        gateway.registerInitiatedFlow(CCDAmendFlow.Acceptor.class);
        rou.registerInitiatedFlow(CCDAmendFlow.Acceptor.class);
    }

    @Before
    public void setup() {
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }


    @Test
    public void greenTest() throws ExecutionException, InterruptedException {
        Party rouParty = rou.getInfo().getLegalIdentities().get(0);

        CCDIssueFlow.Initiator flow = new CCDIssueFlow.Initiator(rouParty, "ACME Corp", new UniqueIdentifier(), 6);
        CordaFuture<SignedTransaction> future = gateway.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTx = future.get();
        logger.info("At the end of my test");

    }
//
    @Test
    public void amendTest() throws ExecutionException, InterruptedException {
        Party rouParty = rou.getInfo().getLegalIdentities().get(0);

        logger.info("Creating");
        CCDIssueFlow.Initiator flowI = new CCDIssueFlow.Initiator(rouParty, "ACME Corp", new UniqueIdentifier(), 6);
        CordaFuture<SignedTransaction> futureI = gateway.startFlow(flowI);
        network.runNetwork();
        SignedTransaction signedTxI = futureI.get();
        CCDState initialState = (CCDState) signedTxI.getTx().getOutputs().get(0).getData();





        logger.info("Amending");
        CCDAmendFlow.Initiator flowA = new CCDAmendFlow.Initiator(initialState.getLinearId(), initialState.getOwner(), initialState.getGleifId(), initialState.getCompany(),9);
        CordaFuture<SignedTransaction> futureA = rou.startFlow(flowA);
        network.runNetwork();
        SignedTransaction signedTx = futureA.get();

    }
//







//
////    @Test
////    public void flowRecordsTheSameTransactionInBothPartyVaults() throws Exception {
////        Party lender = a.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
////        Party borrower = b.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
//////        IOUState iou = new IOUState(Currencies.POUNDS(10), lender, borrower);
//////        IOUIssueFlow.InitiatorFlow flow = new IOUIssueFlow.InitiatorFlow(iou);
////
////        Future<SignedTransaction> future = a.startFlow(flow);
////        mockNetwork.runNetwork();
////        SignedTransaction stx = future.get();
////        System.out.printf("Signed transaction hash: %h\n", stx.getId());
////
////        Arrays.asList(a, b).stream().map(el ->
////                el.getServices().getValidatedTransactions().getTransaction(stx.getId())
////        ).forEach(el -> {
////            SecureHash txHash = el.getId();
////            System.out.printf("$txHash == %h\n", stx.getId());
////            assertEquals(stx.getId(), txHash);
////        });
////    }
}
