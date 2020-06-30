package org.r3.kyc.clients;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.utilities.NetworkHostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class RPCClient {
    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    public static void main(String[] args) {
        // Create an RPC connection to the node.
        if (args.length != 3) throw new IllegalArgumentException("Usage: Client <node address> <rpc username> <rpc password>");
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(args[0]);
        final String rpcUsername = args[1];
        final String rpcPassword = args[2];
        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
        final CordaRPCOps proxy = client.start(rpcUsername, rpcPassword).getProxy();

        // Interact with the node.
        // For example, here we print the nodes on the network.
        final List<NodeInfo> nodes = proxy.networkMapSnapshot();
        logger.info("{}", nodes);
    }
}
