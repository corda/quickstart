package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import org.junit.Test;

import java.util.List;

import static net.corda.testing.driver.Driver.driver;
import static org.junit.Assert.assertEquals;

public class DriverBasedTest {
    private final TestIdentity gateway = new TestIdentity(new CordaX500Name("Gateway", "", "GB"));
    private final TestIdentity rou = new TestIdentity(new CordaX500Name("Arvato", "", "US"));

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
            } catch (Exception e) {
                throw new RuntimeException("Caught exception during test: ", e);
            }

            return null;
        });
    }
}