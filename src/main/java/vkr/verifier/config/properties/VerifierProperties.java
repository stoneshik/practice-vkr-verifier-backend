package vkr.verifier.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "verifier")
public class VerifierProperties {
    private String workingDir;
    private String pythonExecutable;
    private String moduleName;
    private Integer timeoutSeconds;
    private Integer batchSize;
    private Scheduler scheduler = new Scheduler();

    @Data
    public static class Scheduler {
        private Long fixedDelayMs;
    }
}
