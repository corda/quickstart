package org.r3.state;

import com.example.contract.IOUContract;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;
import org.r3.contract.AssetContract;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(AssetContract.class)
public class AssetState implements OwnableState, LinearState {

    private final String title;
    private final String description;
    private final String imageUrl;
    private final UniqueIdentifier linearId;
    private final AbstractParty owner;

    public AssetState(String title, String description, String imageUrl, UniqueIdentifier linearId, AbstractParty owner) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.linearId = linearId;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
        return null;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner);

    }
}
