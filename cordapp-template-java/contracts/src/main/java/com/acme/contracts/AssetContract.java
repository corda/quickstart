package com.acme.contracts;

import com.acme.states.AssetState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.*;


public class AssetContract implements Contract {

    public static final String ID = "com.acme.contracts.AssetContract";
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<Commands> cmd = requireSingleCommand(tx.getCommands(), Commands.class);
        final Set<PublicKey> signers = new HashSet<>(cmd.getSigners());
        if (cmd.getValue() instanceof Commands.Create) {
            requireThat(require -> {
                require.using("Asset creation should have 0 input states.", tx.getInputs().isEmpty());
                require.using("Asset creation should have 1 output states.", tx.getOutputs().size()==1);
                AssetState outState = (AssetState) tx.getOutputStates().get(0);
                require.using("All parties must sign this transaction.",signers.equals(keysFromParticipants(outState)));


                require.using("Owner must be the signer of this state on creation.", outState.getOwner().getOwningKey().equals(signers.iterator().next()));
                return null;
            });


        }
    }



    private Set<PublicKey> keysFromParticipants(AssetState assetState) {
        return assetState
                .getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(Collectors.toSet());
    }


    public interface Commands extends CommandData {
        class Create implements Commands {}
        class Transfer implements Commands {}
    }


}
