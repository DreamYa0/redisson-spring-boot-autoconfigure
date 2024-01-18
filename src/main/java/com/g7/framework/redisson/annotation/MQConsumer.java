package com.g7.framework.redisson.annotation;

import com.g7.framework.redisson.enums.MQModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQConsumer {
    /**
     * topic name
     * @return
     */
    String name();

    /**
     * 匹配模式 <br />
     * PRECISE精准的匹配 如:name="myTopic" 那么发送者的topic name也一定要等于myTopic  <br />
     * PATTERN模糊匹配 如: name="myTopic.*" 那么发送者的topic name 可以是 myTopic.name1 myTopic.name2.尾缀不限定
     * @return
     */
    MQModel model() default MQModel.PRECISE;
}
