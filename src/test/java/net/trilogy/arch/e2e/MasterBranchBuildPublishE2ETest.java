package net.trilogy.arch.e2e;

import org.junit.Test;

import java.io.File;

import static net.trilogy.arch.TestHelper.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MasterBranchBuildPublishE2ETest {

  @Test
  public void publish() throws Exception {
    File documentationRoot = new File("documentation/products/arch-as-code");

    Integer statusCode = execute("publish", documentationRoot.getAbsolutePath());

    assertThat(statusCode, equalTo(0));
  }
}
