package com.example.test.flow;

import net.corda.core.contracts.TransactionState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.AppServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;

import org.r3.state.AssetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.corda.core.identity.Party;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyService extends SingletonSerializeAsToken {
    private final static Logger log = LoggerFactory.getLogger(MyService.class);
    private final static Executor executor = Executors.newFixedThreadPool(8);
    private final AppServiceHub serviceHub;


    public MyService(AppServiceHub serviceHub) {
        this.serviceHub = serviceHub;
        doSomething();
        log.info("Tracking Something");
    }

    private void doSomething() {
        Party ourIdentity = ourIdentity();
        serviceHub.getVaultService().trackBy(AssetState.class).getUpdates().subscribe(
                update -> {
                    update.getProduced().forEach(
                            message -> {
                                TransactionState<AssetState> state = message.getState();
                                if (ourIdentity.equals(
                                        serviceHub.getNetworkMapCache().getPeerByLegalName(new CordaX500Name("BankOperator", "Toronto", "CA"))
                                )) {
                                    executor.execute(() -> {
                                        log.info("Directing to new flow " + state);
                                        //serviceHub.startFlow(new TransferAssetFlow(state)); // START FLOW HERE
                                    });
                                }
                            }
                    );
                }
        );
    }

    private Party ourIdentity() {
        return serviceHub.getMyInfo().getLegalIdentities().get(0);
    }
}
