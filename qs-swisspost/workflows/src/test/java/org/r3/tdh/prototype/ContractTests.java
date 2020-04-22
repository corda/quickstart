package org.r3.tdh.prototype;

import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import org.r3.tdh.prototype.contracts.CCDCommands;
import org.r3.tdh.prototype.contracts.CCDContract;
import org.r3.tdh.prototype.entity.Address;
import org.r3.tdh.prototype.states.CCDState;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.transaction;

public class ContractTests {


    private final TestIdentity issuer = new TestIdentity(new CordaX500Name("Issuer", "", "GB"));
    private final TestIdentity rou = new TestIdentity(new CordaX500Name("ROU", "", "GB"));
    static private final MockServices ledgerServices = new MockServices(asList("org.r3.tdh.prototype.contract", "org.r3.tdh.prototype.flow"));
    //private CCDState ccd = new CCDState(issuer, masterNode, address, linearId, owner, gleifId, company, qal);
    private CCDState ccd;
    @Test
    public void dummyTest() {

    }

    public CCDState setUp(String qal) {

        ccd = new CCDState(
                issuer.getParty(),
                null,
                new Address("Address Line 1", "MyTown"),
                new UniqueIdentifier(),
                rou.getParty(),
                new UniqueIdentifier(),
                "acme corp",
                ""
        );
        return ccd;
    }

    @Test
    public void testHighQuality() {
        CCDState ccdStateOut = setUp("High");
        transaction(ledgerServices, tx -> {
            tx.output(CCDContract.ID, ccdStateOut);
            tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
            tx.verifies();
            return null;
        });

    }
}