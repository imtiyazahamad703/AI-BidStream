package com.bidstream.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuctionLockService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionLockService.class);
    private static final String LOCK_KEY_PREFIX = "auction:%d:lock";
    
    // Configurable timeouts
    private static final long WAIT_TIME_MS = 2000;
    private static final long LEASE_TIME_MS = 3000;

    private final RedissonClient redissonClient;

    public AuctionLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private String getLockKey(Long auctionId) {
        return String.format(LOCK_KEY_PREFIX, auctionId);
    }

    /**
     * Attempts to acquire a distributed lock for the given auction.
     * Blocks for up to WAIT_TIME_MS to acquire the lock.
     * The lock will be automatically released after LEASE_TIME_MS if not explicitly unlocked.
     *
     * @return true if the lock was acquired, false otherwise
     */
    public boolean tryLock(Long auctionId) {
        String lockKey = getLockKey(auctionId);
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean acquired = lock.tryLock(WAIT_TIME_MS, LEASE_TIME_MS, TimeUnit.MILLISECONDS);
            if (acquired) {
                logger.debug("Acquired lock for auction {}", auctionId);
            } else {
                logger.warn("Failed to acquire lock for auction {} within {} ms", auctionId, WAIT_TIME_MS);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while trying to acquire lock for auction {}", auctionId, e);
            return false;
        }
    }

    /**
     * Releases the lock for the given auction if it's currently held by this thread.
     */
    public void unlock(Long auctionId) {
        String lockKey = getLockKey(auctionId);
        RLock lock = redissonClient.getLock(lockKey);
        
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            logger.debug("Released lock for auction {}", auctionId);
        }
    }
}
