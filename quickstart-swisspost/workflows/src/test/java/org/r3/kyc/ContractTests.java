package org.r3.kyc;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.checkerframework.common.aliasing.qual.Unique;
import org.junit.Test;
import org.r3.kyc.contracts.CCDCommands;
import org.r3.kyc.contracts.CCDContract;
import org.r3.kyc.contracts.TemplateContract;
import org.r3.kyc.entity.Address;
import org.r3.kyc.states.CCDState;
import org.r3.kyc.states.TemplateState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;


public class ContractTests {

    private static final Logger logger = LoggerFactory.getLogger(ContractTests.class);



    static private final MockServices ledgerServices = new MockServices(asList("org.r3.kyc.contracts", "org.r3.kyc.flows"));
    static private final TestIdentity gateway = new TestIdentity(new CordaX500Name("Gateway", "London", "GB"));
    static private final TestIdentity rou = new TestIdentity(new CordaX500Name("Arvato", "London", "GB"));



    public Address initAddress() {
        return new Address("12 Test Street", "Testville");
    }
    public String initCompany() {
        return "ACME Corp";
    }

    public CCDState setupCCD(String method) {
        logger.info("Method"+method);
        CCDState ccd;
        switch (method.toLowerCase()) {
            case "testownerissuermatchfailure":
                ccd = new CCDState(
                        gateway.getParty(),
                        null,
                        initAddress(),
                        new UniqueIdentifier(),
                        gateway.getParty(),
                        new UniqueIdentifier(),
                        initCompany(),
                        "Excellent"
                );
                break;
            case "teststandardverifies":
                ccd = new CCDState(
                        gateway.getParty(),
                        null,
                        initAddress(),
                        new UniqueIdentifier(),
                        rou.getParty(),
                        new UniqueIdentifier(),
                        initCompany(),
                        "Excellent"
                );
                break;
            default:
                ccd= new CCDState(gateway.getParty(),
                        null,
                        initAddress(),
                        new UniqueIdentifier(),
                        rou.getParty(),
                        new UniqueIdentifier(),
                        initCompany(),
                        ""
                );
        }
        return ccd;

    }

    /**
     * Prove it works with a dummy transaction
     */
    @Test
    public void dummyTestForTemplateState() {
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

    /**
     * Let's build up the REAL testing here with the CCDContract
     *
     * when the data is issued
     */

    @Test
    public void testStandardVerifies() {
        CCDState ccd = setupCCD(new Throwable().getStackTrace()[0]
                .getMethodName());
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {

                tx.output(CCDContract.ID,
                        ccd
                       );
                tx.command(ImmutableList.of(gateway.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.verifies();


                return null;
            });
            return null;
        }));
    }


    /**
     * Fail on issuer and owner being the same identity
     */
    @Test
    public void testOwnerIssuerMatchFailure() {
        CCDState ccd = setupCCD(new Throwable().getStackTrace()[0]
                .getMethodName());
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {

                tx.output(CCDContract.ID,
                        ccd
                );
                tx.command(ImmutableList.of(gateway.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.failsWith("The Issuer and the owner cannot be the same entity");

                return null;
            });
            return null;
        }));

    }


}