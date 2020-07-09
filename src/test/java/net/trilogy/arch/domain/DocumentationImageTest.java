package net.trilogy.arch.domain;

import net.trilogy.arch.TestHelper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class DocumentationImageTest {

    @Test
    public void shouldIdentifyImages() throws IOException {
        File image = new File(getClass().getResource(TestHelper.IMAGE_THOUGHTWORKS_FILE).getFile());
        File notImage = new File(getClass().getResource(TestHelper.JSON_STRUCTURIZR_EMPTY).getFile());
        File dir = Files.createTempDirectory("aac").toFile();

        assertThat(DocumentationImage.isImage(image), equalTo(true));
        assertThat(DocumentationImage.isImage(notImage), equalTo(false));
        assertThat(DocumentationImage.isImage(dir), equalTo(false));
        assertThat(DocumentationImage.isImage(null), equalTo(false));
    }
}
