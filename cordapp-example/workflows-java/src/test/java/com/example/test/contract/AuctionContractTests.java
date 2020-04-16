package com.example.test.contract;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import org.r3.contract.AssetContract;
import org.r3.contract.AuctionContract;
import org.r3.state.AssetState;
import org.r3.state.AuctionState;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;

public class AuctionContractTests {
    static private final MockServices ledgerServices = new MockServices(asList("org.r3.contract", "org.r3.flow"));
    static private final TestIdentity partyA = new TestIdentity(new CordaX500Name("PartyA", "London", "GB"));
    static private final TestIdentity partyB = new TestIdentity(new CordaX500Name("PartyB", "London", "GB"));


   // public AuctionState(LinearPointer<LinearState> auctionItem,
    // UUID auctionId, \
    // Amount<Currency> basePrice,
    //
    // Amount<Currency> highestBid,
    // Party highestBidder,
    // Instant bidEndTime,
    // Amount<Currency> winningBid,
    // Boolean active,
    // Party auctioneer,
    // List<Party> bidders,
    // Party winner) {

        @Test
    public void canCreateAuctionOnLedger() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
//                AuctionState auctionState = new AuctionState(null,
//                        new UniqueIdentifier(),
//                        Amount.parseCurrency("100 GBP"),
//
//
//                        Amount.parseCurrency("0 GBP"),
//                        true,
//                        LocalDateTime.ofInstant(Instant.now().plusMillis(300000),
//                                java.time.ZoneId.systemDefault()),
//
//
//
//                        null,
//                        null,
//                        null);
//                tx.output(AuctionContract.ID, auctionState);


                tx.command(partyA.getPublicKey(), new AssetContract.Commands.Create());
                return null;
            });
            return null;
        }));
    }



//
//    @Test
//    public void transactionMustIncludeCreateCommand() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.fails();
//                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
//                tx.verifies();
//                return null;
//            });
//            return null;
//        }));
//    }
//
//    @Test
//    public void transactionMustHaveNoInputs() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.input(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
//                tx.failsWith("No inputs should be consumed when issuing an IOU.");
//                return null;
//            });
//            return null;
//        }));
//    }
//
//    @Test
//    public void transactionMustHaveOneOutput() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
//                tx.failsWith("Only one output state should be created.");
//                return null;
//            });
//            return null;
//        }));
//    }
//
//    @Test
//    public void lenderMustSignTransaction() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.command(miniCorp.getPublicKey(), new IOUContract.Commands.Create());
//                tx.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        }));
//    }
//
//    @Test
//    public void borrowerMustSignTransaction() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.command(megaCorp.getPublicKey(), new IOUContract.Commands.Create());
//                tx.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        }));
//    }
//
//    @Test
//    public void lenderIsNotBorrower() {
//        ledger(ledgerServices, (ledger -> {
//            ledger.transaction(tx -> {
//                tx.output(IOUContract.ID, new IOUState(iouValue, megaCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
//                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
//                tx.failsWith("The lender and the borrower cannot be the same entity.");
//                return null;
//            });
//            return null;
//        }));
//    }
//

}