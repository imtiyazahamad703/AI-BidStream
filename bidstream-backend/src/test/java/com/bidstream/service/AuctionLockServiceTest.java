package com.bidstream.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuctionLockServiceTest {

    private RedissonClient redissonClient;
    private RLock lock;
    private AuctionLockService auctionLockService;

    @BeforeEach
    void setUp() {
        redissonClient = Mockito.mock(RedissonClient.class);
        lock = Mockito.mock(RLock.class);
        
        when(redissonClient.getLock("auction:1:lock")).thenReturn(lock);
        
        auctionLockService = new AuctionLockService(redissonClient);
    }

    @Test
    void tryLock_Success_ReturnsTrue() throws InterruptedException {
        when(lock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        
        boolean acquired = auctionLockService.tryLock(1L);
        
        assertTrue(acquired);
        verify(lock).tryLock(2000L, 3000L, TimeUnit.MILLISECONDS);
    }

    @Test
    void tryLock_Failure_ReturnsFalse() throws InterruptedException {
        when(lock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(false);
        
        boolean acquired = auctionLockService.tryLock(1L);
        
        assertFalse(acquired);
    }

    @Test
    void tryLock_Interrupted_ReturnsFalse() throws InterruptedException {
        when(lock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenThrow(new InterruptedException());
        
        boolean acquired = auctionLockService.tryLock(1L);
        
        assertFalse(acquired);
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void unlock_HeldByCurrentThread_Unlocks() {
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        
        auctionLockService.unlock(1L);
        
        verify(lock).unlock();
    }

    @Test
    void unlock_NotHeldByCurrentThread_DoesNothing() {
        when(lock.isHeldByCurrentThread()).thenReturn(false);
        
        auctionLockService.unlock(1L);
        
        verify(lock, never()).unlock();
    }
}
