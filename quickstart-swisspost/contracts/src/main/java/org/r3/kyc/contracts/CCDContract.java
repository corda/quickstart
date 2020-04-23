package org.r3.kyc.contracts;

import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import org.r3.kyc.states.CCDState;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CCDContract implements Contract {

    public static String ID="org.r3.kyc.contracts.CCDContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        doCCD(tx);
    }

    private void doCCD(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CCDCommands> command = requireSingleCommand(tx.getCommands(), CCDCommands.class);
        CCDCommands commandType = command.getValue();
        if (commandType instanceof CCDCommands.Issue) verifyIssue (tx, command);
        else if (commandType instanceof CCDCommands.Amend) verifyAmend (tx, command);
//        else if (commandType instanceof CCDCommands.Approve) verifyApprove (tx, command);
//        else if (commandType instanceof CCDCommands.Publish) verifyPublish (tx, command);

    }

    private void verifyIssue(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("An issuance should not consume any input states", tx.getInputs().size()==0);
            final List<CCDState> outputs = tx.outputsOfType(CCDState.class);
            require.using("Output state should be of type CCDState", outputs.size()==1);

            List<PublicKey> publicKeys = new ArrayList<>();
            for (AbstractParty participant: outputs.get(0).getParticipants()) {
                publicKeys.add(participant.getOwningKey());
            }
            require.using("All participants must be signers", command.getSigners().containsAll(publicKeys));
            return null;
        });

    }

    private void verifyAmend(LedgerTransaction tx, CommandWithParties command) {
        requireThat(require -> {
            require.using("An amend should consume one input state", tx.getInputs().size()==1);
            require.using("There should only be one output state", tx.getOutputs().size()==1);
            final CCDState in = tx.inputsOfType(CCDState.class).get(0);
            require.using("There must only be one signer in an Amend transaction", command.getSigners().size()==1);
            final Party owner = in.getOwner();
            require.using("The current owner must be the signer in an Amend transaction", command.getSigners().containsAll(Arrays.asList()));
            return null;
        });
    }
    private void verifyApprove(LedgerTransaction tx, CommandWithParties command) {

    }
    private void verifyPublish(LedgerTransaction tx, CommandWithParties command) {

    }
}
