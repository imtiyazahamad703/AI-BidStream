package com.bidstream.service;

import com.bidstream.config.KafkaTopicConfig;
import com.bidstream.entity.Bid;
import com.bidstream.event.BidEvent;
import com.bidstream.repository.jpa.BidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BidEventConsumer.class);

    private final BidRepository bidRepository;
    private final RedisBidCacheService redisBidCacheService;

    public BidEventConsumer(BidRepository bidRepository, RedisBidCacheService redisBidCacheService) {
        this.bidRepository = bidRepository;
        this.redisBidCacheService = redisBidCacheService;
    }

    @KafkaListener(topics = KafkaTopicConfig.BID_EVENTS_TOPIC, groupId = "${spring.kafka.consumer.group-id:bid-processor}")
    @Transactional
    public void consumeBidEvent(BidEvent event) {
        logger.info("Consumed BidEvent: {}", event);

        try {
            // Persist the bid event to MySQL
            Bid bid = new Bid();
            bid.setAuctionId(event.getAuctionId());
            bid.setBidderEmail(event.getBidderEmail());
            bid.setAmount(event.getAmount());
            bid.setCreatedAt(event.getTimestamp());
            
            bidRepository.save(bid);
            
            // Re-sync cache just to be safe (already updated proactively by BidService)
            redisBidCacheService.updateHighestBid(event.getAuctionId(), event.getAmount());
            
            logger.debug("Successfully persisted bid and synchronized cache for auction {}", event.getAuctionId());
        } catch (Exception e) {
            logger.error("Error processing BidEvent for auction {}: {}", event.getAuctionId(), e.getMessage(), e);
            throw e; // Rely on Kafka retry mechanism
        }
    }
}
