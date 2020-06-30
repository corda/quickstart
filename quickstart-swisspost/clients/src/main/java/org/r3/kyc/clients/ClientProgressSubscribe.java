package org.r3.kyc.clients;

import kotlin.Pair;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import org.r3.kyc.flows.CCDIssueFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;

/**
 * Connects to a Corda node via RPC and performs RPC operations on the node.
 *
 * The RPC connection is configured using command line arguments.
 */
public class ClientProgressSubscribe {

    private static final String RPC_USERNAME = "user1";
    private static final String RPC_PASSWORD = "test";
    private final static Logger logger = LoggerFactory.getLogger(ClientProgressSubscribe.class);

    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Usage: RpcClient <node address> <counterpartyName>");
        System.out.println(args[0]);
        System.out.println(args[1]);

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

            logger.info("\nSuccessfully issued" + counterpartyName);

        }

        private void issueCounterparty(CordaRPCOps rpcProxy, String counterpartyName) {
            final CordaX500Name counterpartyX500Name = CordaX500Name.parse(counterpartyName);
            logger.info("Fetching "+counterpartyName);
            Party counterparty = rpcProxy.wellKnownPartyFromX500Name(counterpartyX500Name);
            if (counterparty == null) throw new IllegalArgumentException("Peer " + counterpartyName + " not found in " +
                    "the network map");

            //Party otherParty, String companyName, UniqueIdentifier gleifId, int qal
            UniqueIdentifier issueID = new UniqueIdentifier();


            FlowProgressHandle<SignedTransaction> flowFuture  = rpcProxy.startTrackedFlowDynamic(CCDIssueFlow.Initiator.class,
                    counterparty,
                    "ACME Corp",issueID,10);
            Observable<List<String>> dataFeed = flowFuture.getProgress().toList();
            //Observable<List<Pair<Integer, String>>> dataFeed=flowFuture.getStepsTreeFeed().getUpdates();


            // Subscribe and dump out dataFeed updates
            dataFeed.asObservable()
            .toBlocking()
            .subscribe(System.out::println);



        }
        private static void actionToPerformObj(Object o) {
            logger.info("Got something");
        }
        private static void actionToPerform(Object o) {
            logger.info("{}", o.toString());
        }

    }


}