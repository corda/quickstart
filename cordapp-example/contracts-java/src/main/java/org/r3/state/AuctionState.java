package org.r3.state;

import net.corda.core.contracts.*;
import net.corda.core.flows.FlowLogicRefFactory;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.r3.contract.AuctionContract;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@BelongsToContract(AuctionContract.class)
public class AuctionState implements SchedulableState {

    /**
     * Details on auction
     */
    private final UUID auctionId;
    private final Amount<Currency> basePrice;
    private final Amount<Currency> highestBid;

    private final Instant bidEndTime;
    private final Amount<Currency> winningBid;
    private final Boolean active;

    /**
     * Auction Item referenced via LinearPointer and LinearState
     */
    private final LinearPointer<LinearState> auctionItem;
    /**
     * Parties involved
     */
    private final Party auctioneer;
    private final List<Party> bidders;
    private final Party winner;
    private final Party highestBidder;

    public AuctionState(LinearPointer<LinearState> auctionItem, UUID auctionId, Amount<Currency> basePrice, Amount<Currency> highestBid, Party highestBidder, Instant bidEndTime, Amount<Currency> winningBid, Boolean active, Party auctioneer, List<Party> bidders, Party winner) {
        this.auctionItem = auctionItem;
        this.auctionId = auctionId;
        this.basePrice = basePrice;
        this.highestBid = highestBid;
        this.highestBidder = highestBidder;
        this.bidEndTime = bidEndTime;
        this.winningBid = winningBid;
        this.active = active;
        this.auctioneer = auctioneer;
        this.bidders = bidders;
        this.winner = winner;
    }



    @Nullable
    @Override
    public ScheduledActivity nextScheduledActivity(@NotNull StateRef thisStateRef, @NotNull FlowLogicRefFactory flowLogicRefFactory) {
        return null;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public Amount<Currency> getBasePrice() {
        return basePrice;
    }

    public Amount<Currency> getHighestBid() {
        return highestBid;
    }

    public Instant getBidEndTime() {
        return bidEndTime;
    }

    public Amount<Currency> getWinningBid() {
        return winningBid;
    }

    public Boolean getActive() {
        return active;
    }

    public LinearPointer<LinearState> getAuctionItem() {
        return auctionItem;
    }

    public Party getAuctioneer() {
        return auctioneer;
    }

    public List<Party> getBidders() {
        return bidders;
    }

    public Party getWinner() {
        return winner;
    }

    public Party getHighestBidder() {
        return highestBidder;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        List<AbstractParty> allParties = new ArrayList<>(bidders);
        allParties.add(auctioneer);
        return allParties;
    }
}
