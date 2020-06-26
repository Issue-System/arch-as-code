package net.trilogy.arch.config;

import lombok.Getter;
import lombok.Builder;

import java.util.Optional;

@Builder
public class AppConfig {
  @Getter
  @Builder.Default
  private final String logPath = envOrDefault("LOG_PATH", "~/.arch-as-code/log.log");

  private static String envOrDefault(String envName, String orElse) {
    return Optional.ofNullable(System.getenv(envName)).orElse(orElse);
  }
}
