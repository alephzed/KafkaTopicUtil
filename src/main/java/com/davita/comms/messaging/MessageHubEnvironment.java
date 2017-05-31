package com.davita.comms.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageHubEnvironment {

  private String name, label, plan;
  private MessageHubCredentials credentials;

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty
  public void setName(final String name) {
    this.name = name;
  }

  @JsonProperty
  public String getLabel() {
    return label;
  }

  @JsonProperty
  public void setLabel(final String label) {
    this.label = label;
  }

  @JsonProperty
  public String getPlan() {
    return plan;
  }

  @JsonProperty
  public void setPlan(final String plan) {
    this.plan = plan;
  }

  @JsonProperty
  public MessageHubCredentials getCredentials() {
    return credentials;
  }

  @JsonProperty
  public void setCredentials(final MessageHubCredentials credentials) {
    this.credentials = credentials;
  }
}
