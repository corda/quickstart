package com.acme.tests;

import com.acme.contracts.AssetContract;
import com.acme.flows.CreateAssetFlow;
import com.acme.states.AssetState;
import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NetworkParameters;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;
import static org.junit.Assert.assertNotNull;

public class FlowTests {

    private  MockNetwork network;
    private  StartedMockNode a;


    @Before
    public void setup() {
        network = new MockNetwork(
                new MockNetworkParameters(
                        ImmutableList.of(
                                TestCordapp.findCordapp("com.acme.flows"),
                                TestCordapp.findCordapp("com.acme.contracts")
                        )
                ).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                        10485760, 10485760 * 50, Instant.now(), 1,
                        Collections.emptyMap()))
        );
        a = network.createPartyNode(null);

        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Test
    public void testCreateAssetFlow() throws Exception {
        /**
         * A test to ensure that we can initiate a flow and create the asset
         */
        CreateAssetFlow flow = new CreateAssetFlow("Test Asset", "Dummy Asset", "http://abc.com/dummy.png");
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();
        AssetState asset = (AssetState) signedTransaction.getTx().getOutput(0);
        assertNotNull(asset);
    }


}