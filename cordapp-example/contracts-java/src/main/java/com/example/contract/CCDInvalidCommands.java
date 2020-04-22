package com.example.contract;

import net.corda.core.contracts.CommandData;

public interface CCDInvalidCommands extends CommandData  {
    class DoSomethingBad implements CCDInvalidCommands {}

}
