package org.r3.kyc.contracts;

import net.corda.core.contracts.CommandData;

public interface CCDCommands extends CommandData  {
    class Issue implements CCDCommands {}
    class Amend implements CCDCommands {}
    class Approve implements CCDCommands {}
    class Publish implements CCDCommands {}

}
