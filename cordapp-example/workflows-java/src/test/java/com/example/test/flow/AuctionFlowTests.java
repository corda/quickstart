package com.example.test.flow;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.Amount;
import net.corda.core.node.NetworkParameters;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.r3.flow.CreateAssetFlow;
import org.r3.flow.CreateAuctionFlow;
import org.r3.state.AssetState;
import org.r3.state.AuctionState;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class AuctionFlowTests {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    @Before
    public void setup() {
//        network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(ImmutableList.of(
//                TestCordapp.findCordapp("org.r3.contract"),
//                TestCordapp.findCordapp("org.r3.flow"))));

        network = new MockNetwork(
                new MockNetworkParameters(
                        ImmutableList.of(
                                TestCordapp.findCordapp("org.r3.contract"),
                                TestCordapp.findCordapp("org.r3.flow")
                        )
                ).withNetworkParameters(new NetworkParameters(4, Collections.emptyList(),
                        10485760, 10485760 * 50, Instant.now(), 1,
                        Collections.emptyMap()))
        );


        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        // For real nodes this happens automatically, but we have to manually register the flow for tests.
//        for (StartedMockNode node : ImmutableList.of(a, b)) {
//            node.registerInitiatedFlow(CreateAssetFlow.class);
//        }
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();



    @Test
    public void testCreateAssetFlow() throws Exception {
        CreateAssetFlow flow = new CreateAssetFlow("Test Asset", "Dummy Asset", "http://abc.com/dummy.png");
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();
        AssetState asset = (AssetState) signedTransaction.getTx().getOutput(0);
        assertNotNull(asset);

        CreateAuctionFlow.Initiator auctionFlow = new CreateAuctionFlow.Initiator (Amount.parseCurrency("1000 GBP"),
                asset.getLinearId().getId(), LocalDateTime.ofInstant(Instant.now().plusMillis(30000), ZoneId.systemDefault()));
        CordaFuture<SignedTransaction> future1 = a.startFlow(auctionFlow);
        network.runNetwork();
        SignedTransaction transaction = future1.get();
        AuctionState auctionState = (AuctionState) transaction.getTx().getOutput(0);
        assertNotNull(auctionState);
    }
//
//    @Test
//    public void signedTransactionReturnedByTheFlowIsSignedByTheInitiator() throws Exception {
//        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(1, b.getInfo().getLegalIdentities().get(0));
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//
//        SignedTransaction signedTx = future.get();
//        signedTx.verifySignaturesExcept(b.getInfo().getLegalIdentities().get(0).getOwningKey());
//    }
//
//    @Test
//    public void signedTransactionReturnedByTheFlowIsSignedByTheAcceptor() throws Exception {
//        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(1, b.getInfo().getLegalIdentities().get(0));
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//
//        SignedTransaction signedTx = future.get();
//        signedTx.verifySignaturesExcept(a.getInfo().getLegalIdentities().get(0).getOwningKey());
//    }
//
//    @Test
//    public void flowRecordsATransactionInBothPartiesTransactionStorages() throws Exception {
//        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(1, b.getInfo().getLegalIdentities().get(0));
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//        SignedTransaction signedTx = future.get();
//
//        // We check the recorded transaction in both vaults.
//        for (StartedMockNode node : ImmutableList.of(a, b)) {
//            assertEquals(signedTx, node.getServices().getValidatedTransactions().getTransaction(signedTx.getId()));
//        }
//    }
//
//    @Test
//    public void recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() throws Exception {
//        Integer iouValue = 1;
//        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(iouValue, b.getInfo().getLegalIdentities().get(0));
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//        SignedTransaction signedTx = future.get();
//
//        // We check the recorded transaction in both vaults.
//        for (StartedMockNode node : ImmutableList.of(a, b)) {
//            SignedTransaction recordedTx = node.getServices().getValidatedTransactions().getTransaction(signedTx.getId());
//            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
//            assert (txOutputs.size() == 1);
//
//            IOUState recordedState = (IOUState) txOutputs.get(0).getData();
//            assertEquals(recordedState.getValue(), iouValue);
//            assertEquals(recordedState.getLender(), a.getInfo().getLegalIdentities().get(0));
//            assertEquals(recordedState.getBorrower(), b.getInfo().getLegalIdentities().get(0));
//        }
//    }
//
//    @Test
//    public void flowRecordsTheCorrectIOUInBothPartiesVaults() throws Exception {
//        Integer iouValue = 1;
//        ExampleFlow.Initiator flow = new ExampleFlow.Initiator(1, b.getInfo().getLegalIdentities().get(0));
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//        future.get();
//
//        // We check the recorded IOU in both vaults.
//        for (StartedMockNode node : ImmutableList.of(a, b)) {
//            node.transaction(() -> {
//                List<StateAndRef<IOUState>> ious = node.getServices().getVaultService().queryBy(IOUState.class).getStates();
//                assertEquals(1, ious.size());
//                IOUState recordedState = ious.get(0).getState().getData();
//                assertEquals(recordedState.getValue(), iouValue);
//                assertEquals(recordedState.getLender(), a.getInfo().getLegalIdentities().get(0));
//                assertEquals(recordedState.getBorrower(), b.getInfo().getLegalIdentities().get(0));
//                return null;
//            });
//        }
//    }
}