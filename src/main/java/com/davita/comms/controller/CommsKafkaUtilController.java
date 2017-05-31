package com.davita.comms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.davita.comms.messaging.CommsKafkaConsumerUtil;
import com.davita.comms.messaging.TopicProperties;

@RestController
@RequestMapping("/")
public class CommsKafkaUtilController {
  
  @Autowired
  private CommsKafkaConsumerUtil consumerUtil;

  @PostMapping("offset")
  public ResponseEntity<String> updateOffset(@RequestParam(value = "topic") final String topic,
      @RequestParam(value = "group") final String group,
      @RequestParam(value = "offset") final int offset,
      @RequestParam(value = "partition") final int partition) {
    
    final TopicProperties properties =  new TopicProperties();
    properties.setTopic(topic);
    properties.setGroupId(group);
    properties.setPartitions(partition);
    consumerUtil.updateOffset(properties, offset);

    return new ResponseEntity<String>(HttpStatus.CREATED);
  }

}
