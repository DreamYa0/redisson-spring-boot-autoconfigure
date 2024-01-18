package com.g7.framework.redisson.configuration;

import com.g7.framework.redisson.annotation.EnableCache;
import com.g7.framework.redisson.properties.RedissonProperties;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * MQ配置
 */
@EnableCaching
@EnableConfigurationProperties(value = RedissonProperties.class)
public class CacheConfiguration implements ImportAware {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);
    private String[] value;
    /**
     * 缓存时间 默认30分钟
     * @return
     */
    private long ttl;
    /**
     * 最长空闲时间 默认30分钟
     * @return
     */
    private long maxIdleTime;

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public CacheManager cacheManager() {
        Map<String, CacheConfig> config = new HashMap<>();
        // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
        for (String s : value) {
            logger.info("初始化spring cache空间{}", s);
            config.put(s, new CacheConfig(ttl, maxIdleTime));
        }
        return new RedissonSpringCacheManager(redissonClient, config);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableAttrMap = importMetadata
                .getAnnotationAttributes(EnableCache.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.value = enableAttrs.getStringArray("value");
        this.maxIdleTime = enableAttrs.getNumber("maxIdleTime");
        this.ttl = enableAttrs.getNumber("ttl");
    }
}
