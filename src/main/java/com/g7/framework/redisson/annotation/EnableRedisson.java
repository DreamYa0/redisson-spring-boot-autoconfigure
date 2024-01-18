package com.g7.framework.redisson.annotation;

import com.g7.framework.redisson.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author dreamyao
 * @title
 * @date 2019-04-16 21:51
 * @since 1.0.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import(RedissonConfiguration.class)
@Configuration
public @interface EnableRedisson {

}
