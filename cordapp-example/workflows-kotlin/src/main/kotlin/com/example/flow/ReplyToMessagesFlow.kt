package com.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.example.state.IOUState

import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.transactions.SignedTransaction
import org.r3.services.MessageRepository
import org.r3.state.MessageState

@InitiatingFlow
@StartableByRPC
class ReplyToMessagesFlow  {
//    @Suspendable
//    override fun call(): List<SignedTransaction> {
//      //  return messages().map { reply(it) }
//        return null;
//    }
//
//    private fun messages() =
//            repository().findAll(PageSpecification(1, 100))
//                    .states
//                    .filter { it.state.data.borrower == ourIdentity }
//
//    private fun repository() = serviceHub.cordaService(MessageRepository::class.java)
//
//    //@Suspendable
//    //private fun reply(message: StateAndRef<IOUState>) = subFlow(SendMessageFlow(response(message), message))
//
//    private fun response(message: StateAndRef<MessageState>): MessageState {
//        val state = message.state.data
//        return state.copy(
//                contents = "Thanks for your message: ${state.contents}",
//                recipient = state.sender,
//                sender = state.recipient
//        )
//    }
}