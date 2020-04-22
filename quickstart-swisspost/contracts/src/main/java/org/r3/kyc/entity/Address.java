package org.r3.kyc.entity;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class Address {
    private final String addressLine1;
    private final String town;

    public Address(String addressLine1, String town) {
        this.addressLine1 = addressLine1;
        this.town = town;
    }
}
