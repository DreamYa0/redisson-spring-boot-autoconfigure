package com.g7.framework.redisson.aop;

import com.g7.framework.redisson.annotation.MQProducer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MQ发送消息AOP
 */
@Aspect
public class MQAop {

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(mq)")
    public void aspect(MQProducer mq) {
    }

    @Around("aspect(mq)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint, MQProducer mq) {
        try {
            Object obj = proceedingJoinPoint.proceed();
            RTopic topic = redissonClient.getTopic(mq.name());
            topic.publish(obj);
            return obj;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}
