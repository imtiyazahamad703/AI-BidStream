package com.bidstream.service;

import com.bidstream.config.KafkaTopicConfig;
import com.bidstream.event.BidEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BidEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(BidEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BidEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBidEvent(BidEvent bidEvent) {
        // Use auctionId as the partition key to guarantee order per auction
        String key = String.valueOf(bidEvent.getAuctionId());
        
        logger.debug("Publishing BidEvent to topic {}: {}", KafkaTopicConfig.BID_EVENTS_TOPIC, bidEvent);
        kafkaTemplate.send(KafkaTopicConfig.BID_EVENTS_TOPIC, key, bidEvent);
    }
}
