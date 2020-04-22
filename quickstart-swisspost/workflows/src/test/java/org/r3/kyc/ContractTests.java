package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import org.r3.kyc.contracts.CCDCommands;
import org.r3.kyc.contracts.TemplateContract;
import org.r3.kyc.states.TemplateState;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;


public class ContractTests {
    static private final MockServices ledgerServices = new MockServices(asList("org.r3.kyc.contracts", "org.r3.kyc.flows"));
    static private final TestIdentity gateway = new TestIdentity(new CordaX500Name("Gateway", "London", "GB"));
    static private final TestIdentity rou = new TestIdentity(new CordaX500Name("Arvato", "London", "GB"));

    @Test
    public void dummyTest() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {

                tx.output(TemplateContract.ID, new TemplateState());
                tx.command(ImmutableList.of(gateway.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }


}