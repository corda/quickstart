package org.r3.tdh.kyc.states;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SampleState implements ContractState {
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}
