package com.davita.comms.messaging;

/**
 * A POJO for the topic properties
 *
 * Each app which plans to communicate with the Kafka broker needs to populate
 * this with details for the each individual topic
 *
 */
public class TopicProperties {
  /** the name of the topic */
  private String topic;
  /**
   * the clientId for the topic, see kafka docs
   * {@https://kafka.apache.org/documentation/#oldconsumerconfigs}
   */
  private String clientId;
  /**
   * the groupId of the kafka topic, see kafka docs
   * {@https://kafka.apache.org/documentation/#oldconsumerconfigs}
   */
  private String groupId;
  /**
   * the partitions needed for this topic, the same number of partitions needs
   * to be configured while creating the topic in kafka broker
   */
  private int partitions;
  /**
   * this is a consumer client specific property, which specifies how many
   * consumer threads will be used to process messages from the kafka
   * topic. @see Consumer on how it is used
   */
  private int consumerThreads;

  public String getTopic() {
    return topic;
  }

  public void setTopic(final String topic) {
    this.topic = topic;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(final String clientId) {
    this.clientId = clientId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  public int getPartitions() {
    return partitions;
  }

  public void setPartitions(final int partitions) {
    this.partitions = partitions;
  }

  public int getConsumerThreads() {
    return consumerThreads;
  }

  public void setConsumerThreads(final int consumerThreads) {
    this.consumerThreads = consumerThreads;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((clientId == null) ? 0 : clientId.hashCode());
    result = (prime * result) + ((topic == null) ? 0 : topic.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TopicProperties other = (TopicProperties) obj;
    if (clientId == null) {
      if (other.clientId != null) {
        return false;
      }
    } else if (!clientId.equals(other.clientId)) {
      return false;
    }
    if (topic == null) {
      if (other.topic != null) {
        return false;
      }
    } else if (!topic.equals(other.topic)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TopicProperties [topic=" + topic + ", clientId=" + clientId + ", groupId=" + groupId
        + ", partitions=" + partitions + ", consumerThreads=" + consumerThreads + "]";
  }

}
