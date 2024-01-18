package com.g7.framework.redisson.configuration;

import com.g7.framework.redisson.mq.RedissonMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MQ配置
 */
public class MQConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedissonMQListener.class)
    public RedissonMQListener RedissonMQListener() {
        return new RedissonMQListener();
    }
}
