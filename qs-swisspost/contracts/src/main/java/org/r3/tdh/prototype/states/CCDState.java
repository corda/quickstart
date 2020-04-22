package org.r3.tdh.prototype.states;

//import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;
import org.r3.tdh.prototype.contracts.CCDContract;
import org.r3.tdh.prototype.entity.Address;

import java.util.Arrays;
import java.util.List;


@BelongsToContract(CCDContract.class)
public class CCDState implements LinearState {


    private final Party issuer;
    private final Party masterNode;
    private final Address address;
    private final UniqueIdentifier linearId;
    private final Party owner;

    // metadata
    private final UniqueIdentifier gleifId; // Legal entity identifier
    private final String company;

    // Additional meta fields on to master node
    private final String qal;   // quality assurance level High Medium Low


    public Party getOwner() {
        return owner;
    }
//
//    public CCDState(String qal, Party issuer, Party masterNode, Address address, UniqueIdentifier linearId, Party owner, UniqueIdentifier gleifId, String company) {
//        this.qal = qal;
//        this.issuer = issuer;
//        this.masterNode = masterNode;
//        this.address = address;
//        this.linearId = linearId;
//        this.owner = owner;
//        this.gleifId = gleifId;
//        this.company = company;
//    }

    public CCDState(Party issuer, Party masterNode, Address address, UniqueIdentifier linearId, Party owner, UniqueIdentifier gleifId, String company, String qal) {

        this.issuer = issuer;
        this.masterNode = masterNode;
        this.address = address;
        this.linearId = linearId;
        this.owner = owner;
        this.gleifId = gleifId;
        this.company = company;
        this.qal = qal;
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(issuer, owner);
    }

    // Setters


    public Party getIssuer() {
        return issuer;
    }

    public Party getMasterNode() {
        return masterNode;
    }

    public Address getAddress() {
        return address;
    }

    public UniqueIdentifier getGleifId() {
        return gleifId;
    }

    public String getCompany() {
        return company;
    }

    public String getQal() {
        return qal;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
}
