package com.bidstream.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class CacheConfigTest {

    @Test
    void cacheManager_CreatesManager() {
        CacheConfig cacheConfig = new CacheConfig();
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        
        CacheManager manager = cacheConfig.cacheManager(factory);
        
        assertNotNull(manager);
    }
}
