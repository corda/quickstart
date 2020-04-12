package com.example.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.example.state.IOUState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import static net.corda.core.contracts.ContractsDSL.requireThat;


public class MessageFlow {

    @InitiatingFlow
    @StartableByRPC

    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final Party otherParty;

        public Initiator(Party otherParty) {
            this.otherParty = otherParty;

        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            FlowSession investigatorSession = initiateFlow(otherParty);
            investigatorSession.send("hello");
            return null;
        }

//        private String receive(FlowSession session) {
//            // Receive something here
//            getLogger().info("Got here");
//            return "received";
//        }
    }


    @InitiatedBy(MessageFlow.Initiator.class)
    public static class Acceptor extends FlowLogic<SignedTransaction> {

        private FlowSession otherPartySession = null;


        public Acceptor(FlowSession otherPartySession) {
            System.out.println("Acceptor");
            this.otherPartySession = otherPartySession;
        }


        @Override
        public SignedTransaction call() throws FlowException {
            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    System.out.println("Check Tx");
                    requireThat(require -> {

                        return null;
                    });
                }
            }
            return null;
        }
    }
}
