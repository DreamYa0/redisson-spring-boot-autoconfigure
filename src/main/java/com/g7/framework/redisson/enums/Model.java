package com.g7.framework.redisson.enums;

public enum Model {

    /**
     * 哨兵
     */
    SENTINEL,
    /**
     * 主从
     */
    MASTERSLAVE,
    /**
     * 单例
     */
    SINGLE,
    /**
     * 集群
     */
    CLUSTER,
    /**
     * 云托管模式
     */
    REPLICATED
}
