package com.davita.comms;

import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@Configuration
@Profile("kafka")
@ConfigurationProperties
public class KafkaConfigurations {

  @Bean(name = "consumerProperties")
  public Properties consumerProperties() throws IOException {
    return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/consumer.properties"));
  }

}
