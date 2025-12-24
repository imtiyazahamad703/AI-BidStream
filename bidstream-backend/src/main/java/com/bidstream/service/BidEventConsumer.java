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
    private final AuctionEventPublisher auctionEventPublisher;

    public BidEventConsumer(BidRepository bidRepository, RedisBidCacheService redisBidCacheService, AuctionEventPublisher auctionEventPublisher) {
        this.bidRepository = bidRepository;
        this.redisBidCacheService = redisBidCacheService;
        this.auctionEventPublisher = auctionEventPublisher;
    }

    @KafkaListener(topics = KafkaTopicConfig.BID_EVENTS_TOPIC, groupId = "${spring.kafka.consumer.group-id:bid-processor}")
    @Transactional
    public void consumeBidEvent(BidEvent event) {
        logger.info("Consumed BidEvent: {}", event);

        try {
            // Get previous highest bidder from DB/cache.
            Bid latestBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(event.getAuctionId());
            String previousBidder = (latestBid != null && !latestBid.getBidderEmail().equals(event.getBidderEmail())) ? latestBid.getBidderEmail() : null;

            // Persist the bid event to MySQL
            Bid bid = new Bid();
            bid.setAuctionId(event.getAuctionId());
            bid.setBidderEmail(event.getBidderEmail());
            bid.setAmount(event.getAmount());
            bid.setCreatedAt(event.getTimestamp());
            
            bidRepository.save(bid);
            
            // Re-sync cache just to be safe (already updated proactively by BidService)
            redisBidCacheService.updateHighestBid(event.getAuctionId(), event.getAmount(), event.getBidderEmail());
            
            if (event.getTrackingId() != null) {
                redisBidCacheService.updateBidStatus(event.getTrackingId(), "ACCEPTED");
            }
            
            // Broadcast live bid to connected WebSocket clients
            auctionEventPublisher.publishBidPlaced(event.getAuctionId(), event.getAmount(), event.getBidderEmail());
            
            if (previousBidder != null) {
                auctionEventPublisher.publishOutbidNotification(event.getAuctionId(), previousBidder, event.getAmount());
            }
            
            logger.debug("Successfully persisted bid and synchronized cache for auction {}", event.getAuctionId());
        } catch (Exception e) {
            if (event.getTrackingId() != null) {
                redisBidCacheService.updateBidStatus(event.getTrackingId(), "FAILED");
            }
            logger.error("Error processing BidEvent for auction {}: {}", event.getAuctionId(), e.getMessage(), e);
            throw e; // Rely on Kafka retry mechanism
        }
    }
}
