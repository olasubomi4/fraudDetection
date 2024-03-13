package com.example.fraudDetection.config;

import com.example.fraudDetection.entity.FlaggedUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    Cache<String, FlaggedUser> flaggedUsersCache() {
        return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }
}
