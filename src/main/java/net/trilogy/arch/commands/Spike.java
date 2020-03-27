package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.in.google.GoogleDocumentReader;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "spike")
public class Spike implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        new GoogleDocumentReader().doAThing();
        return 0;
    }
}
