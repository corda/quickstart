package com.example.test.driver

import net.corda.client.rpc.internal.RPCClient
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.finance.flows.CashIssueAndPaymentFlow
import net.corda.finance.flows.CashPaymentFlow
import net.corda.node.services.Permissions
import net.corda.testing.core.TestIdentity
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.driver
import net.corda.testing.node.User

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class DriverBasedTests {
    val bankA = TestIdentity(CordaX500Name("BankA", "", "GB"))
    val bankB = TestIdentity(CordaX500Name("BankB", "", "US"))

    @Test
    fun `node test`() {
        driver(DriverParameters(isDebug = true, startNodesInProcess = true)) {
            // This starts two nodes simultaneously with startNode, which returns a future that completes when the node
            // has completed startup. Then these are all resolved with getOrThrow which returns the NodeHandle list.
            val (partyAHandle, partyBHandle) = listOf(
                    startNode(providedName = bankA.name),
                    startNode(providedName = bankB.name)
            ).map { it.getOrThrow() }

            // This test makes an RPC call to retrieve another node's name from the network map, to verify that the
            // nodes have started and can communicate. This is a very basic test, in practice tests would be starting
            // flows, and verifying the states in the vault and other important metrics to ensure that your CorDapp is
            // working as intended.
            assertEquals(partyAHandle.rpc.wellKnownPartyFromX500Name(bankB.name)!!.name, bankB.name)
            assertEquals(partyBHandle.rpc.wellKnownPartyFromX500Name(bankA.name)!!.name, bankA.name)
        }
    }

    @Test
    fun `run driver test`() {
        val bankA = TestIdentity(CordaX500Name("Bank A", "", "GB"))
        val bankB = TestIdentity(CordaX500Name("Bank B", "", "GB"))
        val bankC = TestIdentity(CordaX500Name("Bank C", "", "GB"))

        val user = User("user1", "test", permissions = setOf("ALL"))

        driver(DriverParameters().withStartNodesInProcess(true)) {
            val (_, nodeAHandle, _) = listOf(
                    startNode(providedName = bankA.name, rpcUsers = listOf(user)),
                    startNode(providedName = bankB.name, rpcUsers = listOf(user))
            ).map { it.getOrThrow() }

            val nodeARpcAddress = nodeAHandle.rpcAddress.toString()
            val nodeARpcClient = RpcClient(nodeARpcAddress)

            // We can ping Bank B...
            nodeARpcClient.ping(bankB.name.toString())
            // ...but not Bank C, who isn't on the network
            assertFailsWith<IllegalArgumentException> {
                nodeARpcClient.ping(bankC.name.toString())
            }
        }
    }
}
