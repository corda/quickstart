package org.r3.kyc;

import net.corda.testing.driver.DriverParameters;
import org.junit.Test;
import static org.junit.Assert.*;

import static net.corda.testing.driver.Driver.driver;
import static net.corda.testing.node.internal.InternalTestUtilsKt.FINANCE_CORDAPPS;

public class IntegrationTest1 {

    @Test
    public void test1Start() {
        driver(new DriverParameters()
                .withStartNodesInProcess(true)
                .withCordappsForAllNodes(FINANCE_CORDAPPS), dsl -> {

        });
        assertEquals("","x");
    }

}
