package com.davita.comms.messaging;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;



/**
 * This is an implementation of @see MessageHubBroker for local profile.
 *
 * In the local profile, this class takes care of creating the connection to the
 * locally running kafka broker. All the configuration properties are being read
 * from the @see IKafkaProperties provided by the app which is using this config
 *
 * Local profile does not need setting up of SSL connection using JAAS
 * connection
 *
 */

@Profile("local")
@Component
public class MessageHubLocalBroker implements MessageHubBroker {

  @Autowired(required = false)
  @Qualifier("kafkaProperties")
  private IKafkaProperties kafkaProperties;

  public static Logger logger = Logger.getLogger(MessageHubLocalBroker.class);

  public MessageHubLocalBroker() {

  }

  @Override
  public Properties getClientConfiguration(final TopicProperties topicProperties,
      final boolean isProducer) {
    final Properties props = new Properties();
    // props.put("metadata.broker.list", kafkaProperties.getKafkaHost());
    // props.put("request.required.acks", "-1");
    final String brokers = String.join(",", kafkaProperties.getKafkaHosts());
    props.put("bootstrap.servers", brokers);
    // props.put("client.id", topicProperties.getClientId());
    // TODO move this to properties of topic or kafka properties
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    // for consumer
    if (!isProducer) {
      props.put("group.id", topicProperties.getGroupId());
      props.put("enable.auto.commit", false);
      props.put("auto.offset.reset", "latest");
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    }
    return props;
  }

}
