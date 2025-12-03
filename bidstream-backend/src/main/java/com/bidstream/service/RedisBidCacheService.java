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

    private final StringRedisTemplate redisTemplate;

    public RedisBidCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getHighestBidKey(Long auctionId) {
        return String.format(HIGHEST_BID_KEY_PREFIX, auctionId);
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
     * Updates the highest bid in Redis.
     */
    public void updateHighestBid(Long auctionId, Double amount) {
        redisTemplate.opsForValue().set(getHighestBidKey(auctionId), amount.toString());
        logger.debug("Updated Redis highest bid for auction {} to {}", auctionId, amount);
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
}
