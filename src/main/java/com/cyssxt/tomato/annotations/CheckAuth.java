package com.cyssxt.tomato.annotations;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target(value= ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface CheckAuth {

    String value() default "";
    Class processor() default Object.class;
}
