package net.trilogy.arch.e2e;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import net.trilogy.arch.Application;
import net.trilogy.arch.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;


public class ParentCommandE2ETest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void rootCommandShouldPrintUsage() throws Exception {
        collector.checkThat(
                execute(),
                equalTo(0)
        );

        collector.checkThat(
                out.toString(),
                containsString("Usage:")
        );
    }


    @Test
    public void rootCommandShouldLogOutputToFileButNotConsole() throws Exception {
        // WHEN
        var file = File.createTempFile("aac", "");
        var app = Application.builder().appConfig(AppConfig.builder().logPath(file.getAbsolutePath()).build()).build();
        app.execute(new String[]{});

        // THEN
        collector.checkThat(
                out.toString(),
                not(containsString("INFO"))
        );
        collector.checkThat(
                Files.readString(file.toPath()),
                containsString("INFO : Usage")
        );

        FileUtils.forceDelete(file);
    }

}
