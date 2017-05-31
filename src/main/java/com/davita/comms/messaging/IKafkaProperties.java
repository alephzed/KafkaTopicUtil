package com.davita.comms.messaging;

/**
 *
 * This is a generic interface which each app which needs to connect to kafka
 * needs to implement.
 *
 * Individual apps needs to have this configuration properties read from a
 * configuration file like yml
 *
 */
public interface IKafkaProperties {

  public String getUserName();

  public void setUserName(String user);

  public String getPassword();

  public void setPassword(String password);

  public String getApiKey();

  public void setApiKey(String apiKey);

  public String[] getKafkaHosts();

  public void setKafkaHosts(String[] kafkaHosts);

  public String getRestHost();

  public void setRestHost(String restHost);

}
