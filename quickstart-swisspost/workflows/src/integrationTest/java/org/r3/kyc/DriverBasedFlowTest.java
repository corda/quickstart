package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.node.services.Permissions;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.driver.DriverDSL;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static net.corda.testing.driver.Driver.driver;
import static org.junit.Assert.assertEquals;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static net.corda.testing.core.TestConstants.ALICE_NAME;
import static net.corda.testing.driver.Driver.driver;
import static net.corda.testing.node.internal.InternalTestUtilsKt.cordappWithPackages;
import static org.junit.Assert.assertEquals;
import java.util.concurrent.Future;
import net.corda.core.utilities.KotlinUtilsKt;
import org.r3.kyc.flows.CCDIssueFlow;

public class DriverBasedFlowTest {
    private final TestIdentity gateway = new TestIdentity(new CordaX500Name("Gateway", "", "GB"));
    private final TestIdentity rou = new TestIdentity(new CordaX500Name("Arvato", "", "US"));



 //   @Test
//    public void summingWorks() {
//        driver(new DriverParameters(Collections.singletonList(cordappWithPackages("org.r3.kyc.flows"))), (DriverDSL dsl) -> {
//            User gwUser = new User("gwUser", "testPassword1", singleton(Permissions.all()));
//
//            Future<NodeHandle> gwFuture = dsl.startNode(new NodeParameters()
//                    .withProvidedName(gateway.getName())
//                    .withRpcUsers(singletonList(gwUser))
//            );
//            NodeHandle gw = KotlinUtilsKt.getOrThrow(gwFuture, null);
//            CordaRPCClient gwClient = new CordaRPCClient(gw.getRpcAddress());
//            CordaRPCOps gwProxy = gwClient.start("gwUser", "testPassword1").getProxy();
//
//
//            //CCDIssueFlow.Initiator flow = new CCDIssueFlow.Initiator(rou.getParty(), "ACME Corp",
//                   // new UniqueIdentifier(), 456);
//            final  CCDIssueFlow.Initiator flowRequest = new CCDIssueFlow.Initiator(rou.getParty(), "ACME Corp",
//                    new UniqueIdentifier(), 456);
//            CordaFuture<SignedTransaction> future =
//                    (CordaFuture<SignedTransaction>) gwProxy.startFlowDynamic(CCDIssueFlow.Initiator.class
//                            );
//
//
//            //CordaFuture<SignedTransaction> answerFuture =
//              //      gwProxy.startFlowDynamic(CCDIssueFlow.Initiator.class).getReturnValue();
//
//            // int answer = KotlinUtilsKt.getOrThrow(answerFuture, null);
//           // assertEquals(3, answer);
//            assertEquals(true,true);
//            //return null;
//        });
//    }


    @Test
    public void nodeTest2() {
        driver(new DriverParameters().withIsDebug(true).withStartNodesInProcess(true), dsl -> {


            // Start a pair of nodes and wait for them both to be ready.
            List<CordaFuture<NodeHandle>> handleFutures = ImmutableList.of(
                    dsl.startNode(new NodeParameters().withProvidedName(gateway.getName())),
                    dsl.startNode(new NodeParameters().withProvidedName(rou.getName()))
            );

            try {
                NodeHandle partyGatewayHandle = handleFutures.get(0).get();

                NodeHandle partyRouHandle = handleFutures.get(1).get();

                // From each node, make an RPC call to retrieve another node's name from the network map, to verify that the
                // nodes have started and can communicate.

                // This is a very basic test: in practice tests would be starting flows, and verifying the states in the vault
                // and other important metrics to ensure that your CorDapp is working as intended.
                assertEquals(partyGatewayHandle.getRpc().wellKnownPartyFromX500Name(rou.getName()).getName(), rou.getName());
                //assertEquals(partyBHandle.getRpc().wellKnownPartyFromX500Name(bankA.getName()).getName(), bankA.getName());


                /**
                 * Now we are going to connect using the test users
                 */
                final  CCDIssueFlow.Initiator flowRequest = new CCDIssueFlow.Initiator(rou.getParty(), "ACME Corp",
                    new UniqueIdentifier(), 456);
//                CordaFuture<SignedTransaction> answerFuture =
//                    gateway..startFlowDynamic(CCDIssueFlow.Initiator.class).getReturnValue();
            } catch (Exception e) {
                throw new RuntimeException("Caught exception during test: ", e);
            }

            return null;
        });
    }

    @Test
    public void nodeTest() {
        driver(new DriverParameters().withIsDebug(true).withStartNodesInProcess(true), dsl -> {


            // Start a pair of nodes and wait for them both to be ready.
            List<CordaFuture<NodeHandle>> handleFutures = ImmutableList.of(
                    dsl.startNode(new NodeParameters().withProvidedName(gateway.getName())),
                    dsl.startNode(new NodeParameters().withProvidedName(rou.getName()))
            );

            try {
                NodeHandle partyGatewayHandle = handleFutures.get(0).get();

                NodeHandle partyRouHandle = handleFutures.get(1).get();

                // From each node, make an RPC call to retrieve another node's name from the network map, to verify that the
                // nodes have started and can communicate.

                // This is a very basic test: in practice tests would be starting flows, and verifying the states in the vault
                // and other important metrics to ensure that your CorDapp is working as intended.
                assertEquals(partyGatewayHandle.getRpc().wellKnownPartyFromX500Name(rou.getName()).getName(), rou.getName());
                //assertEquals(partyBHandle.getRpc().wellKnownPartyFromX500Name(bankA.getName()).getName(), bankA.getName());


                /**
                 * Now we are going to connect using the test users
                 */


            } catch (Exception e) {
                throw new RuntimeException("Caught exception during test: ", e);
            }

            return null;
        });
    }
}