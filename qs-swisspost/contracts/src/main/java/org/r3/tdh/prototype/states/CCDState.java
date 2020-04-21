package org.r3.tdh.prototype.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import org.r3.tdh.prototype.contracts.CCDContract;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(CCDContract.class)
public class CCDState implements ContractState {

    public CCDState() {

    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList();
    }
}