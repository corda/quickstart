package com.acme.tests;

import com.acme.contracts.AssetContract;
import com.acme.states.AssetState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import static java.util.Arrays.*;

import static net.corda.testing.node.NodeTestUtils.ledger;

public class ContractTests {
    static private final MockServices ledgerServices = new MockServices(asList("com.acme.contracts", "com.acme.flows"));
    static private final TestIdentity partyA = new TestIdentity(new CordaX500Name("Party A", "London", "GB"));
    static private final TestIdentity partyB = new TestIdentity(new CordaX500Name("Party B", "London", "GB"));

    @Test
    public void canCreateAssetOnLedger() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(AssetContract.ID, new AssetState(new UniqueIdentifier(),"new title", "new description","", partyA.getParty()));
                tx.command(partyA.getPublicKey(), new AssetContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }



}