package net.trilogy.arch.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name="architecture-update", aliases = {"au"})
public class ArchitectureUpdateCommand {
    @Command(name="initialize", aliases={"init"})
    void initialize(){

    }
}
