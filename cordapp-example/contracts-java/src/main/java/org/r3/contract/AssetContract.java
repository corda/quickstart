package org.r3.contract;

import com.example.contract.IOUContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import org.r3.state.AssetState;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import static net.corda.core.contracts.ContractsDSL.*;
import static java.util.stream.Collectors.toSet;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;
import static net.corda.core.contracts.Structures.withoutIssuer;

public class AssetContract implements Contract {

    public static final String ID = "org.r3.contract.AssetContract";


    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() == 0){
            throw new IllegalArgumentException("One command Expected");
        }

        CommandWithParties<Commands> cmd = requireSingleCommand(tx.getCommands(), Commands.class);
        final Set<PublicKey> signers = new HashSet<>(cmd.getSigners());
        if (cmd.getValue() instanceof Commands.Create) {
            requireThat(require -> {
                require.using("Asset creation should have 0 input state",tx.getInputs().isEmpty());
                require.using("Asset creation should have 1 output state", tx.getOutputs().size()==1);
                AssetState outState = (AssetState) tx.getOutputStates().get(0);
                System.out.println("State 1: "+outState);
                System.out.println("State Participants: "+outState.getParticipants().toString());

                require.using("All parties must sign this transaction", signers.equals(keysFromParticipants(outState)));
                require.using("Owner must be the signer of this state on creation.", outState.getOwner().getOwningKey().equals(signers.iterator().next()));

                return null;
            });
        }
    }


    private Set<PublicKey> keysFromParticipants(AssetState state) {
        System.out.println("State: "+state.getParticipants().toString());
        return state
                .getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(toSet());
    }


    public interface Commands extends CommandData {
        class Create implements Commands {}
        class Transfer implements Commands {}
    }



}
