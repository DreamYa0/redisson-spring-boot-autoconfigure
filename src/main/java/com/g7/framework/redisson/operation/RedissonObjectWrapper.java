package com.g7.framework.redisson.operation;

import com.g7.framwork.common.util.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author dreamyao
 * @title
 * @date 2019-11-23 17:49
 * @since 1.0.0
 */
public class RedissonObjectWrapper {

    private static final Logger logger = LoggerFactory.getLogger(RedissonObjectWrapper.class);
    private final RedissonObject redissonObject;

    public RedissonObjectWrapper(RedissonObject redissonObject) {
        this.redissonObject = redissonObject;
    }

    public <T> T wraper(String cacheKey, long expiredTime, Supplier<T> supplier) {
        T cacheValue = redissonObject.getValue(cacheKey);
        if (Objects.nonNull(cacheValue)) {
            logger.info("cache key is [{}] , get cache value is [{}]", cacheKey, JsonUtils.toJson(cacheValue));
            return cacheValue;
        }

        T t = supplier.get();
        if (Objects.nonNull(t)) {
            redissonObject.setValue(cacheKey, t, expiredTime);
            logger.info("put cache value is [{}] , expire time is [{}] ms", t, expiredTime);
        }

        return t;
    }
}
