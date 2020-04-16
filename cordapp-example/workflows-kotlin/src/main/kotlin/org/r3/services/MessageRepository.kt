package org.r3.services

import com.example.state.IOUState
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.utilities.loggerFor
import net.corda.core.serialization.SingletonSerializeAsToken as SingletonSerializeAsToken

class MessageRepository(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    private companion object {
        val log = loggerFor<MessageRepository>()
    }

    init {
        log.info("Repo is alive")
    }
    fun findAll(pageSpec: PageSpecification): Vault.Page<IOUState> =
            serviceHub.vaultService.queryBy(QueryCriteria.LinearStateQueryCriteria(), pageSpec)

}