package com.davita.comms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.davita.comms.messaging.IKafkaProperties;



@Profile("kafka")
@Component(value="kafkaProperties")
@ConfigurationProperties(prefix="kafka")
public class KafkaProperties implements IKafkaProperties {

	private static final Logger log = LoggerFactory.getLogger(KafkaProperties.class);


    private String userName;


    private String password;


    private String apiKey;


    private String[] kafkaHosts;


    private String restHost;



  

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

  

    @Override
    public String[] getKafkaHosts() {
		return kafkaHosts;
	}

	@Override
  public void setKafkaHosts(final String[] kafkaHosts) {
		this.kafkaHosts = kafkaHosts;
	}

	@Override
  public String getRestHost() {
        return restHost;
    }

    @Override
    public void setRestHost(final String restHost) {
        this.restHost = restHost;
    }

	public KafkaProperties() {
		super();
		log.info("KafkaProperties initailized!!!!!!");
	}

	@Override
  public String getUserName() {
		return userName;
	}

	@Override
  public void setUserName(final String userName) {
		this.userName = userName;
		log.info("KafkaProperties User Set initailized!!!!!! "+userName);
	}

	@Override
	public String toString() {
		return "KafkaProperties [userName=" + userName + ", password=" + password + ", apiKey=" + apiKey
				+ ", kafkaHosts=" + kafkaHosts + ", restHost=" + restHost + "]";
	}
}
