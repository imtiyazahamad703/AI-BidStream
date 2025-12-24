package com.bidstream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisBidCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisBidCacheService.class);
    private static final String HIGHEST_BID_KEY_PREFIX = "auction:%d:highestBid";
    private static final String HIGHEST_BIDDER_KEY_PREFIX = "auction:%d:highestBidder";
    private static final String AUCTION_STATE_KEY_PREFIX = "auction:%d:state";
    private static final String BID_STATUS_KEY_PREFIX = "bid_status:%s";

    private final StringRedisTemplate redisTemplate;

    public RedisBidCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getHighestBidKey(Long auctionId) {
        return String.format(HIGHEST_BID_KEY_PREFIX, auctionId);
    }
    
    private String getHighestBidderKey(Long auctionId) {
        return String.format(HIGHEST_BIDDER_KEY_PREFIX, auctionId);
    }
    
    private String getAuctionStateKey(Long auctionId) {
        return String.format(AUCTION_STATE_KEY_PREFIX, auctionId);
    }

    /**
     * Gets the current highest bid from Redis.
     */
    public Optional<Double> getHighestBid(Long auctionId) {
        String val = redisTemplate.opsForValue().get(getHighestBidKey(auctionId));
        if (val != null) {
            try {
                return Optional.of(Double.parseDouble(val));
            } catch (NumberFormatException e) {
                logger.error("Failed to parse highest bid from Redis for auction: {}", auctionId, e);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the current highest bidder from Redis.
     */
    public Optional<String> getHighestBidder(Long auctionId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(getHighestBidderKey(auctionId)));
    }

    /**
     * Updates the highest bid in Redis.
     */
    public void updateHighestBid(Long auctionId, Double amount, String userEmail) {
        redisTemplate.opsForValue().set(getHighestBidKey(auctionId), amount.toString());
        if (userEmail != null) {
            redisTemplate.opsForValue().set(getHighestBidderKey(auctionId), userEmail);
        }
        logger.debug("Updated Redis highest bid for auction {} to {} by {}", auctionId, amount, userEmail);
    }

    /**
     * Updates the bid status in Redis.
     */
    public void updateBidStatus(String trackingId, String status) {
        redisTemplate.opsForValue().set(String.format(BID_STATUS_KEY_PREFIX, trackingId), status);
    }

    /**
     * Gets the bid status from Redis.
     */
    public String getBidStatus(String trackingId) {
        return redisTemplate.opsForValue().get(String.format(BID_STATUS_KEY_PREFIX, trackingId));
    }

    /**
     * Initializes the highest bid state in Redis if it doesn't exist.
     */
    public void initializeAuctionState(Long auctionId, Double initialBid) {
        Boolean set = redisTemplate.opsForValue().setIfAbsent(getHighestBidKey(auctionId), initialBid.toString());
        if (Boolean.TRUE.equals(set)) {
            logger.info("Initialized Redis highest bid for auction {} to {}", auctionId, initialBid);
        }
    }

    /**
     * Gets the cached auction state (e.g., ACTIVE, COMPLETED).
     */
    public Optional<String> getAuctionState(Long auctionId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(getAuctionStateKey(auctionId)));
    }

    /**
     * Updates the cached auction state.
     */
    public void updateAuctionState(Long auctionId, String status) {
        redisTemplate.opsForValue().set(getAuctionStateKey(auctionId), status);
        logger.debug("Updated Redis auction state for auction {} to {}", auctionId, status);
    }
}
