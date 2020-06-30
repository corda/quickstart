package org.r3.kyc.clients;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import org.r3.kyc.flows.CCDIssueFlow;
import org.r3.kyc.states.CCDState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.corda.core.contracts.UniqueIdentifier;


/**
 * Connects to a Corda node via RPC and performs RPC operations on the node.
 *
 * The RPC connection is configured using command line arguments.
 */
public class ClientVaultSubscribe {
    private static final String RPC_USERNAME = "user1";
    private static final String RPC_PASSWORD = "test";
    private final static Logger logger = LoggerFactory.getLogger(ClientVaultSubscribe.class);

    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Usage: RpcClient <node address> <counterpartyName>");

        final String rpcAddressString = args[0];
        final String counterpartyName = args[1];

        final RPCClient rpcClient = new RPCClient(rpcAddressString);
        rpcClient.issue(counterpartyName);
        rpcClient.closeRpcConnection();
    }

    /** An example RPC client that connects to a node and performs various example operations. */
    static class RPCClient {
        public static Logger logger = LoggerFactory.getLogger(RPCClient.class);

        private CordaRPCConnection rpcConnection;

        /** Sets a [CordaRPCConnection] to the node listening on [rpcPortString]. */
        protected RPCClient(String rpcAddressString) {
            final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(rpcAddressString);
            final CordaRPCClient client = new CordaRPCClient(nodeAddress);

            rpcConnection = client.start(RPC_USERNAME, RPC_PASSWORD);
        }

        protected void closeRpcConnection() {
            rpcConnection.close();
        }

        protected void issue(String counterpartyName) {
            CordaRPCOps rpcProxy = rpcConnection.getProxy();
            issueCounterparty(rpcProxy, counterpartyName);

            logger.info("\nSuccessfully issued to " + counterpartyName);

        }

        private void issueCounterparty(CordaRPCOps rpcProxy, String counterpartyName) {

            final CordaX500Name counterpartyX500Name = CordaX500Name.parse(counterpartyName);
            logger.info("Fetching "+counterpartyName);
            Party counterparty = rpcProxy.wellKnownPartyFromX500Name(counterpartyX500Name);
            if (counterparty == null) throw new IllegalArgumentException("Peer " + counterpartyName + " not found in " +
                    "the network map");

            // Set up GUID to pass in as gleifId
            UniqueIdentifier issueID = new UniqueIdentifier();
            logger.info("Starting CCDIssueFlow Dynamic");
            CordaFuture<SignedTransaction> flowFuture  = rpcProxy.startTrackedFlowDynamic(CCDIssueFlow.Initiator.class,
                    counterparty,
                    "ACME Corp",issueID,10).getReturnValue();




            // Set up a data feed which uses rpc to track the vault for CCDState updates
            logger.info("Getting feed to vault");
            DataFeed<Vault.Page<CCDState>, Vault.Update<CCDState>> dataFeed = rpcProxy.vaultTrack(CCDState.class);

            // Now
            logger.info("Getting vault snapshot");
            Vault.Page<CCDState> snapshot = dataFeed.getSnapshot();
            rx.Observable<Vault.Update<CCDState>> vaultUpdates = dataFeed.getUpdates();

            // call a method for each CCDState already in the vault
            logger.info("Getting snapshot update to actionToPerform");
            snapshot.getStates().forEach(RPCClient::actionToPerform);

            // Now we create a blocking step just for the purpose of demonstration
            // You will need to send this off to another thread potentially
            // Please see
            logger.info("Subscribing to vault");
            vaultUpdates.toBlocking().subscribe(update -> update.getProduced().forEach(RPCClient::actionToPerform));


        }
        private static void actionToPerform(StateAndRef<CCDState> state) {
            logger.info("{}", ">>"+state.getState().getData());

        }

    }


}