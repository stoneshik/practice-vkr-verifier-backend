package vkr.verifier.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import vkr.verifier.config.properties.StorageProperties;
import vkr.verifier.config.properties.VerifierProperties;

@Configuration
@EnableConfigurationProperties({
    StorageProperties.class,
    VerifierProperties.class
})
public class PropertiesConfig {}
