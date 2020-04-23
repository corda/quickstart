package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import org.r3.kyc.flows.CCDAmendFlow;
import org.r3.kyc.flows.CCDIssueFlow;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.r3.kyc.states.CCDState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class IssueFlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
        TestCordapp.findCordapp("org.r3.kyc.contracts"),
        TestCordapp.findCordapp("org.r3.kyc.flows")
    )));

    private final static Logger logger = LoggerFactory.getLogger(IssueFlowTests.class);

    private final StartedMockNode gateway = network.createNode();
    private final StartedMockNode rou = network.createNode();

    // Use the corda-training piece to bolt this on

    public IssueFlowTests() {
        gateway.registerInitiatedFlow(CCDIssueFlow.Acceptor.class);
        rou.registerInitiatedFlow(CCDIssueFlow.Acceptor.class);
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
        assert(signedTx.getId()!=null);

    }



     @Test
    public void redTest() throws ExecutionException, InterruptedException {
        Party rouParty = rou.getInfo().getLegalIdentities().get(0);

        CCDIssueFlow.Initiator flow = new CCDIssueFlow.Initiator(rouParty, "ACME Corp", new UniqueIdentifier(), 3);
        CordaFuture<SignedTransaction> future = gateway.startFlow(flow);

        Exception e = null;
        try {
            network.runNetwork();
            SignedTransaction signedTx = future.get();
        } catch (Exception exception) {
            e = exception;
        }
        assert(e!=null);
        // If we want to parse the standard contract error then comment out this line
       assert e.getMessage().equals("net.corda.core.flows.FlowException: java.lang.IllegalArgumentException: Failed requirement: Quality should be above 5");
         // If we want to test our specific error then write the try catch in the flow and comment this line above and comment out the line below
       //         assert e.getMessage().equals("net.corda.core.flows.FlowException: Problem");

    }

}
