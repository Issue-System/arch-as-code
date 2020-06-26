package net.trilogy.arch.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.net.URI;
import java.nio.file.Paths;

public class LoggingConfigurationFactory extends ConfigurationFactory {

  private final String logFilePath;

  public LoggingConfigurationFactory(String logFilePath) {
    this.logFilePath = Paths.get(logFilePath).normalize().toString();
  }

  Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
    builder.setConfigurationName(name);
    builder.setStatusLevel(Level.TRACE);

    AppenderComponentBuilder appenderBuilder = builder.newAppender("file-appender", "File").
            addAttribute("fileName", logFilePath);
    appenderBuilder.addAttribute("immediateFlush", true);
    appenderBuilder.addAttribute("append", true);
    appenderBuilder.add(builder.newLayout("PatternLayout").
            addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));

    builder.add(appenderBuilder);
    builder.add(builder.newRootLogger(Level.TRACE).add(builder.newAppenderRef("file-appender")));
    return builder.build();
  }

  @Override
  public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
    return getConfiguration(loggerContext, source.toString(), null);
  }

  @Override
  public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
    ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
    return createConfiguration(name, builder);
  }

  @Override
  protected String[] getSupportedTypes() {
    return new String[]{"*"};
  }
}
