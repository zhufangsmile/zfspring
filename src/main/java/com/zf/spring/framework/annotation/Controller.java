package com.zf.spring.framework.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Type;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
