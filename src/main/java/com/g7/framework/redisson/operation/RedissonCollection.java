package com.g7.framework.redisson.operation;

import com.g7.framework.redisson.properties.RedissonProperties;
import com.g7.framwork.common.util.json.JsonUtils;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 操作集合
 */
public class RedissonCollection {

    private static final Logger logger = LoggerFactory.getLogger(RedissonCollection.class);
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedissonProperties redissonProperties;

    /**
     * 获取map集合
     * @param name redis of key
     * @param <K> type of key
     * @param <V> type of value
     * @return
     */
    public <K, V> RMap<K, V> getMap(String name) {
        long start = System.currentTimeMillis();
        RMap<K, V> map = redissonClient.getMap(name);
        logger.info("get map cache key is [{}] , use time [{}] ms", name, System.currentTimeMillis() - start);
        return map;
    }

    /**
     * 设置map集合
     * @param name redis of key
     * @param data 数据
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public <K,V> void setMapValues(String name, Map<K,V> data, Long time) {
        long start = System.currentTimeMillis();
        RMap<K,V> map = redissonClient.getMap(name);
        Long dataValidTime = redissonProperties.getDataValidTime();
        if (time != -1) {
            map.expire(dataValidTime, TimeUnit.MILLISECONDS);
        }
        map.putAll(data);
        logger.info("set map value cache key is [{}] , value is [{}],expire time is [{}] ms,use time [{}] ms", name,
                JsonUtils.toJson(data), time, System.currentTimeMillis() - start);
    }

    /**
     * 设置map集合 (过期时间统一从Apollo配置中心配置的过期时间)
     * @param name redis of key
     * @param data 数据
     */
    public <K,V> void setMapValues(String name, Map<K,V> data) {
        setMapValues(name, data, redissonProperties.getDataValidTime());
    }

    /**
     * 获取List集合
     * @param name redis of key
     * @return RList<T>
     */
    public <T> RList<T> getList(String name) {
        long start = System.currentTimeMillis();
        RList<T> list = redissonClient.getList(name);
        logger.info("get list cache key is [{}] , use time [{}] ms", name, System.currentTimeMillis() - start);
        return list;
    }

    /**
     * 设置List集合
     * @param name redis of key
     * @param data 数据
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public <T> void setListValues(String name, List<T> data, Long time) {
        long start = System.currentTimeMillis();
        RList<T> list = redissonClient.getList(name);
        Long dataValidTime = redissonProperties.getDataValidTime();
        if (time != -1) {
            list.expire(dataValidTime, TimeUnit.MILLISECONDS);
        }
        list.addAll(data);
        logger.info("set list value cache key is [{}] , value is [{}],expire time is [{}] ms,use time [{}] ms", name,
                JsonUtils.toJson(data), time, System.currentTimeMillis() - start);
    }

    /**
     * 设置List集合 (过期时间统一从Apollo配置中心配置的过期时间)
     * @param name redis of key
     * @param data 数据
     */
    public <T> void setListValues(String name, List<T> data) {
        setListValues(name, data, redissonProperties.getDataValidTime());
    }

    /**
     * 获取set集合
     * @param name redis of key
     * @return RSet<T>
     */
    public <T> RSet<T> getSet(String name) {
        long start = System.currentTimeMillis();
        RSet<T> set = redissonClient.getSet(name);
        logger.info("get set cache key is [{}] , use time [{}] ms", name, System.currentTimeMillis() - start);
        return set;
    }

    /**
     * 设置set集合
     * @param name redis of key
     * @param data 数据
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public <T> void setSetValues(String name, Set<T> data, Long time) {
        long start = System.currentTimeMillis();
        RSet<T> set = redissonClient.getSet(name);
        Long dataValidTime = redissonProperties.getDataValidTime();
        if (time != -1) {
            set.expire(dataValidTime, TimeUnit.MILLISECONDS);
        }
        set.addAll(data);
        logger.info("set set value cache key is [{}] , value is [{}],expire time is [{}] ms,use time [{}] ms", name,
                JsonUtils.toJson(data), time, System.currentTimeMillis() - start);
    }

    /**
     * 设置set集合
     * @param name redis of key
     * @param data 数据
     */
    public <T> void setSetValues(String name, Set<T> data) {
        setSetValues(name, data, redissonProperties.getDataValidTime());
    }
}
