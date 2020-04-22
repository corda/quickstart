package com.example.contract;

import com.example.state.CCDState;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;


import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CCDContract implements Contract {

    public static String ID="com.example.contract.CCDContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) {
        doCCD(tx);
    }

    private void doCCD(LedgerTransaction tx) {
        CommandWithParties<CCDCommands> command = requireSingleCommand(tx.getCommands(), CCDCommands.class);
        CCDCommands commandType = command.getValue();
        if (commandType instanceof CCDCommands.Issue) verifyIssue (tx, command);
        else if (commandType instanceof CCDCommands.Amend) verifyAmend (tx, command);
//        else if (commandType instanceof CCDCommands.Approve) verifyApprove (tx, command);
//        else if (commandType instanceof CCDCommands.Publish) verifyPublish (tx, command);
        else throw new IllegalArgumentException("Command not recognised.");
    }

    private void verifyIssue(LedgerTransaction tx, CommandWithParties command) {
        requireThat(require -> {
            require.using("An issuance should not consume any input states", tx.getInputs().size()==0);
            final List<CCDState> outputs = tx.outputsOfType(CCDState.class);
            require.using("Output state should be of type CCDState", outputs.size()==1);
            require.using("The Issuer and the owner cannot be the same entity", outputs.get(0).getIssuer()!=outputs.get(0).getOwner());
            List<PublicKey> publicKeys = new ArrayList<>();
            for (AbstractParty participant: outputs.get(0).getParticipants()) {
                publicKeys.add(participant.getOwningKey());
            }
            ArrayList<String> levels = new ArrayList<String>( Arrays.asList( "high" , "medium" , "low" ) );

            require.using("All participants must be signers", command.getSigners().containsAll(publicKeys));
            require.using ("Quality Assurance Level must be valid", !levels.contains(outputs.get(0).getQal().toLowerCase()));

            return null;
        });

    }

    private void verifyAmend(LedgerTransaction tx, CommandWithParties command)  {
        requireThat(require -> {
            // @TODO Test by adding in 0 input states
            require.using("An issuance should consume one input states", tx.getInputs().size()==0);
            final List<CCDState> outputs = tx.outputsOfType(CCDState.class);
            require.using("Output state should be of type CCDState", outputs.size()==1);
            CCDState inputState= (CCDState) tx.getInputs().get(0).getState().getData();
            //CCDState outputState = outputs.get(0);
            require.using("Address should be filled in", inputState.getAddress()==null);
            return null;
        });
    }
    private void verifyApprove(LedgerTransaction tx, CommandWithParties command) {

    }
    private void verifyPublish(LedgerTransaction tx, CommandWithParties command) {

    }
}
