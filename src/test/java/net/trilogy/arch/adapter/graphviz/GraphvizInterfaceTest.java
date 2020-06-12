package net.trilogy.arch.adapter.graphviz;


import java.io.IOException;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GraphvizInterfaceTest {
    @Test
    public void shouldRenderSvg() throws Exception {
        var dir = Files.createTempDirectory("aac");
        String dot = "digraph G { a -> b; }";

        new GraphvizInterface().render(dot, dir.resolve("file.svg"));

        var expected = "<svg width=\"62px\" height=\"116px\"\n viewBox=\"0.00 0.00 62.00 116.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1.0 1.0) rotate(0) translate(4 112)\">\n<title>G</title>\n<polygon fill=\"white\" stroke=\"transparent\" points=\"-4,4 -4,-112 58,-112 58,4 -4,4\"/>\n<!-- a -->\n<g id=\"node1\" class=\"node\">\n<title>a</title>\n<ellipse fill=\"none\" stroke=\"black\" cx=\"27\" cy=\"-90\" rx=\"27\" ry=\"18\"/>\n<text text-anchor=\"middle\" x=\"27\" y=\"-85.8\" font-family=\"Times,serif\" font-size=\"14.00\">a</text>\n</g>\n<!-- b -->\n<g id=\"node2\" class=\"node\">\n<title>b</title>\n<ellipse fill=\"none\" stroke=\"black\" cx=\"27\" cy=\"-18\" rx=\"27\" ry=\"18\"/>\n<text text-anchor=\"middle\" x=\"27\" y=\"-13.8\" font-family=\"Times,serif\" font-size=\"14.00\">b</text>\n</g>\n<!-- a&#45;&gt;b -->\n<g id=\"edge1\" class=\"edge\">\n<title>a&#45;&gt;b</title>\n<path fill=\"none\" stroke=\"black\" d=\"M27,-71.7C27,-63.98 27,-54.71 27,-46.11\"/>\n<polygon fill=\"black\" stroke=\"black\" points=\"30.5,-46.1 27,-36.1 23.5,-46.1 30.5,-46.1\"/>\n</g>\n</g>\n</svg>\n";

        assertThat(dir.resolve("file.svg").toFile().exists(), is(true));
        assertThat(Files.readString(dir.resolve("file.svg")), equalTo(expected));
    }
}
