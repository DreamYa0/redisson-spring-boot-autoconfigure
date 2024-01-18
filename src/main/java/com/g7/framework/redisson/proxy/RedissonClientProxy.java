package com.g7.framework.redisson.proxy;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author dreamyao
 * @title
 * @date 2019-05-29 11:38
 * @since 1.0.0
 */
public class RedissonClientProxy implements InvocationHandler {

    private final Object target;

    public RedissonClientProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object cacheKey = args[0];
        String methodName = method.getName();

        try {

            return ReflectionUtils.invokeMethod(method, target, args);

        } catch (Exception e) {
            throw e;
        }
    }
}
