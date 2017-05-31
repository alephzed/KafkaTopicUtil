package com.davita.comms.messaging;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * This is a @see Runnable consumer class which will be executed by individual
 * thread from the thread pool
 *
 * @author taroy
 *
 */
@Component
public class CommsKafkaConsumerUtil{

	private static final Logger logger = LoggerFactory.getLogger(CommsKafkaConsumerUtil.class);

	
	
	@Autowired
	private MessageHubBroker broker;


	public void updateOffset(final TopicProperties properties,final int offset) {

	  final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(broker.getClientConfiguration(properties, false));
	  
	  final TopicPartition partition = new TopicPartition(properties.getTopic(), properties.getPartitions());
	  
	  final List<TopicPartition> topicList = new ArrayList<TopicPartition>();
	  topicList.add(partition);
	  
	  kafkaConsumer.assign(topicList);
	  
	  long position = kafkaConsumer.position(partition);
    logger.info("current Position: " + position);
		
		kafkaConsumer.seek(partition, offset);
		
		position = kafkaConsumer.position(partition);
    logger.info("current Position: " + position);
    
    kafkaConsumer.commitSync();
		
		kafkaConsumer.close();


	}

}
