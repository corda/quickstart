package com.example.test.contract;

import com.example.contract.CCDCommands;
import com.example.contract.CCDContract;
import com.example.contract.CCDInvalidCommands;
import com.example.contract.IOUContract;
import com.example.entity.Address;
import com.example.state.CCDState;
import com.example.state.IOUState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;

public class CCDContractTests {
    static private final MockServices ledgerServices = new MockServices(asList("com.example.contract", "com.example.flow"));
    static private final TestIdentity issuer = new TestIdentity(new CordaX500Name("Issuer", "London", "GB"));
    static private final TestIdentity rou = new TestIdentity(new CordaX500Name("ROU", "London", "GB"));
    static private final int iouValue = 1;
    private CCDState ccd;
    public CCDState setUp(String qal) {

        ccd = new CCDState(
                issuer.getParty(),
                null,
                null,
                new UniqueIdentifier(),
                rou.getParty(),
                new UniqueIdentifier(),
                "acme corp",
                ""
        );
        return ccd;
    }
    @Test
    public void transactionIsValid() {
        CCDState ccdStateOut = setUp("High");
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(CCDContract.ID, ccdStateOut);
                tx.fails();
                tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

//    @Test
    public void transactionMustIncludeValidCommand() {
        CCDState ccdStateOut = setUp("High");
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(CCDContract.ID, ccdStateOut);
                tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDInvalidCommands.DoSomethingBad());
                tx.fails();
                //tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());

                return null;
            });
            return null;
        }));
    }

//    @Test
//    public void transactionFailsWithInvalidQuality() {
//        CCDState ccdStateOut = setUp("Invalid");
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(CCDContract.ID, ccdStateOut);
//                tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
//                tx.fails();
//
//
//                return null;
//            });
//            return null;
//        }));
//    }

    @Test
    public void transactionSucceedsWithValidQuality() {
        CCDState ccdStateOut = setUp("High");
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(CCDContract.ID, ccdStateOut);
                tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.verifies();


                return null;
            });
            return null;
        }));
    }


    @Test
    public void transactionFailsWithNoAddress() {
        CCDState ccdStateOut = setUp("High");
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(CCDContract.ID, ccdStateOut);
                tx.command(Arrays.asList(issuer.getPublicKey(), rou.getPublicKey()), new CCDCommands.Issue());
                tx.verifies();
//                tx.input(CCDContract.ID, tx.
//                tx.failsWith("Address should be filled in");


                return null;
            });
            return null;
        }));
    }



}