package org.r3.contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class AuctionContract implements Contract {

    public static final String ID = "org.r3.contract.AuctionContract";
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        if (tx.getCommands().size() ==0) {
            throw new IllegalArgumentException("Expected only one command");
        }
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Create implements Commands {}
    }
}
