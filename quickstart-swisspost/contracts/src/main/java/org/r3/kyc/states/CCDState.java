package org.r3.kyc.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;
import org.r3.kyc.contracts.CCDContract;
import org.r3.kyc.entity.Address;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(CCDContract.class)
public class CCDState implements LinearState{


    private Party issuer;
    private  Party masterNode;
    private Address address;
    private UniqueIdentifier linearId;
    private Party owner;

    // metadata
    private UniqueIdentifier gleifId; // Legal entity identifier
    private String company;


    // Additional meta fields on to master node
    private int qal;   // quality assurance level High Medium Low

    public CCDState(Party issuer, Party masterNode, Address address, UniqueIdentifier linearId, Party owner, UniqueIdentifier gleifId, String company, int qal) {
        this.issuer = issuer;
        this.masterNode = masterNode;
        this.address = address;
        this.linearId = linearId;
        this.owner = owner;
        this.gleifId = gleifId;
        this.company = company;
        this.qal = qal;
    }



    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(issuer, owner);
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getMasterNode() {
        return masterNode;
    }

    public Address getAddress() {
        return address;
    }

    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public Party getOwner() {
        return owner;
    }

    public UniqueIdentifier getGleifId() {
        return gleifId;
    }

    public String getCompany() {
        return company;
    }

    public int getQal() {
        return qal;
    }


    public void setIssuer(Party issuer) {
        this.issuer = issuer;
    }

    public void setMasterNode(Party masterNode) {
        this.masterNode = masterNode;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public void setGleifId(UniqueIdentifier gleifId) {
        this.gleifId = gleifId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setQal(int qal) {
        this.qal = qal;
    }



}
