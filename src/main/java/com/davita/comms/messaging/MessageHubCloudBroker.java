package com.davita.comms.messaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is an implementation of @see MessageHubBroker for cloud profile.
 *
 * In the cloud profile, this class takes care of creating the connection to the
 * IBM BLuemix Message Bus(implemented in Kafka). All the configuration
 * properties are being read from the <b>VCAP_SERVICES</b> provided by a bluemix
 * app injected with message hub service
 *
 * the local-cloud profile is used to test pointing to IBM kafka broker from
 * local machine
 *
 * @author taroy
 *
 */

@Profile(value = { "cloud", "local-cloud" })
@Component
public class MessageHubCloudBroker implements MessageHubBroker {

  public static Logger logger = Logger.getLogger(MessageHubCloudBroker.class);
  private static final String JAAS_CONFIG_PROPERTY = "java.security.auth.login.config";
  public static String userDir = System.getProperty("user.dir"); // Current
  // working
  // directory
  public static String resourceDir = userDir + File.separator + "resources";

  /**
   * the @see IKafkaProperties for that specific app using this broker its set
   * to required = false, because if not set and <b>VCAP_SERVICES</b> is set
   * the properties would read from that
   */
  @Autowired(required = false)
  @Qualifier("kafkaProperties")
  private IKafkaProperties kafkaProperties;

  /**
   * the producer properties for a given app, this is a type of @see
   * Properties with key value pairs needed
   *
   * again its set to required = false , because an app may only initialize
   * the broker for consuming from kafka for e.g the AuditService
   */
  @Autowired(required = false)
  @Qualifier(value = "producerProperties")
  private Properties producerProperties;

  /**
   * the consumer properties for a given app, this is a type of @see
   * Properties with key value pairs needed
   *
   * again its set to required = false , because an app may only initialize
   * the broker for producing to kafka
   */
  @Autowired(required = false)
  @Qualifier(value = "consumerProperties")
  private Properties consumerProperties;

  @PostConstruct
  public void init() throws Exception {
    logger.debug("User directory: " + userDir);
    logger.debug("Resource directory: " + resourceDir);
    try {
      setupEnvironment();
    } catch (final Exception e) {
      logger.fatal("Error creating MessageHub environment");
      throw e;
    }
    logger.info("MessageHubCloudBroker initialized successfully!!!!!!");
  }

  /**
   * Sets up the environment, which includes connection to kafka broker in
   * cloud with the values given in VCAP_SERVICES and producer/consumer
   * properties
   *
   * @throws Exception
   */
  private void setupEnvironment() throws Exception {
    logger.debug("Initializing Kafka MessageHubCloudBroker Environment");

    // Set JAAS configuration property.
    if (System.getProperty(JAAS_CONFIG_PROPERTY) == null) {
      System.setProperty(JAAS_CONFIG_PROPERTY, userDir + File.separator + "jaas.conf");
    }

    if (System.getProperty("java.security.auth.login.config") == null) {
      System.setProperty("java.security.auth.login.config", "");
    }

    final String vcapServices = System.getenv("VCAP_SERVICES");
    final ObjectMapper mapper = new ObjectMapper();

    logger.debug("VCAP_SERVICES: \n" + vcapServices);

    if ((vcapServices != null) && !vcapServices.isEmpty()) {
      try {
        // Parse VCAP_SERVICES into Jackson JsonNode, then map the
        // 'messagehub' entry
        // to an instance of MessageHubEnvironment.
        final JsonNode vcapServicesJson = mapper.readValue(vcapServices, JsonNode.class);
        final ObjectMapper envMapper = new ObjectMapper();
        String vcapKey = null;
        final Iterator<String> it = vcapServicesJson.fieldNames();

        // Find the Message Hub service bound to this application.
        while (it.hasNext() && (vcapKey == null)) {
          final String potentialKey = it.next();

          if (potentialKey.startsWith("messagehub")) {
            logger.debug("Using the '" + potentialKey + "' key from VCAP_SERVICES.");
            vcapKey = potentialKey;
          }
        }

        if (vcapKey == null) {
          logger.error(
              "Error while parsing VCAP_SERVICES: A Message Hub service instance is not bound to this application.");
          return;
        }

        final MessageHubEnvironment messageHubEnvironment = envMapper.readValue(
            vcapServicesJson.get(vcapKey).get(0).toString(), MessageHubEnvironment.class);

        final MessageHubCredentials credentials = messageHubEnvironment.getCredentials();
        logger.debug("Message Hub Credentials " + credentials);
        logger.debug("Kafka properties " + kafkaProperties);
        updateJaasConfiguration(credentials);

        kafkaProperties.setKafkaHosts(credentials.getKafkaBrokersSasl());
        kafkaProperties.setRestHost(credentials.getKafkaRestUrl());
        kafkaProperties.setApiKey(credentials.getApiKey());


      } catch (final Exception e) {
        logger.fatal("VCAP_SERVICES reading failure", e);
        throw e;
      }
    } else if ((kafkaProperties != null) && (kafkaProperties.getKafkaHosts() != null)
        && (kafkaProperties.getRestHost() != null) && (kafkaProperties.getApiKey() != null)) {
      logger.debug("Using kafka properties from YAML properties file..." + kafkaProperties);
      updateJaasConfiguration(kafkaProperties.getUserName(), kafkaProperties.getPassword());

      // check for availibility of topic

    } else {
      logger.error("VCAP_SERVICES environment variable is null.");
      return;
    }

  }

  /**
   * Retrieve client configuration information, using a properties file, for
   * connecting to secure Kafka.
   *
   * @param topicProperties
   *          {TopicProperties} Topic properties class read from
   *          application.yml.
   * @param isProducer
   *          {Boolean} Flag used to determine whether or not the
   *          configuration is for a producer.
   * @return {Properties} A properties object which stores the client
   *         configuration info.
   */
  @Override
  public Properties getClientConfiguration(final TopicProperties topicProperties,
      final boolean isProducer) {
    Properties props = null;

    if (isProducer) {
      if ((producerProperties == null) || producerProperties.isEmpty()) {
        throw new RuntimeException("Kafka Producer Properties is not Set");
      }
      props = producerProperties;
    } else {
      if ((consumerProperties == null) || consumerProperties.isEmpty()) {
        throw new RuntimeException("Kafka Consumer Properties is not Set");
      }
      props = consumerProperties;
    }

    final String brokers = String.join(",", kafkaProperties.getKafkaHosts());
    props.put("bootstrap.servers", brokers);
    // props.put("client.id", topicProperties.getClientId());

    if (!isProducer) {
      props.put("group.id", topicProperties.getGroupId());
    }

    if (System.getenv("VCAP_SERVICES") != null) {
      props.put("ssl.truststore.location", userDir + "/.java/jre/lib/security/cacerts");
    }

    logger.debug("Using properties: " + props);

    return props;
  }

  /**
   * Updates JAAS config file with provided credentials.
   *
   * @param credentials
   *          {MessageHubCredentials} Object which stores Message Hub
   *          credentials retrieved from the VCAP_SERVICES environment
   *          variable.
   */
  private static void updateJaasConfiguration(final MessageHubCredentials credentials)
      throws Exception {
    updateJaasConfiguration(credentials.getUser(), credentials.getPassword());
  }

  /**
   *
   * Updates JAAS config file with provided credentials.
   *
   * @param username
   *          {String} the username of beused to connect to Kafka broker.
   * @param password
   *          {String} the password of be used to connect to Kafka broker.
   * @throws Exception
   */
  private static void updateJaasConfiguration(final String username, final String password)
      throws Exception {
    OutputStream jaasStream = null;

    logger.info("Updating JAAS configuration");

    try {
      final ClassPathResource sourceresource = new ClassPathResource(
          "/templates/jaas.conf.template");
      final ClassPathResource destresource = new ClassPathResource("/jaas.conf");
      final File targetFile = new File(destresource.getPath());
      final StringWriter writer = new StringWriter();

      final InputStream initialStream = sourceresource.getInputStream();

      IOUtils.copy(initialStream, writer);
      final String jaasTemplate = writer.toString();

      final String fileContents = jaasTemplate.replace("$USERNAME", username).replace("$PASSWORD",
          password);

      jaasStream = new FileOutputStream(targetFile);
      IOUtils.write(fileContents, jaasStream);

    } catch (final IOException e) {
      logger.error("Writing to JAAS config file:", e);
      throw e;
    } finally {
      if (jaasStream != null) {
        try {
          jaasStream.close();
        } catch (final Exception e) {
          logger.error("Closing JAAS config file:", e);
          throw e;
        }
      }
    }
  }

}
