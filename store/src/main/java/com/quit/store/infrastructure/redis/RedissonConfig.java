package com.quit.store.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisProperties redisProperties;
    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redisson = null;
        Config config = new Config();
        config.useSingleServer()
                .setAddress(
                REDISSON_HOST_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword());
        redisson = Redisson.create(config);
        return redisson;
    }

}
