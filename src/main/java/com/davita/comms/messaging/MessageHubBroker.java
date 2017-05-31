package com.davita.comms.messaging;

import java.util.Properties;


/**
 * A <b>MessageHubBroker</b> defines the core configurations for connecting to a
 * kafka broker
 *
 *
 */
public interface MessageHubBroker {

  public Properties getClientConfiguration(TopicProperties topicProperties, boolean isProducer);

}
