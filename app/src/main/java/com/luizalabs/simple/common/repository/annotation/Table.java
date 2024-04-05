package com.luizalabs.simple.common.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public @Retention(RetentionPolicy.RUNTIME) @interface Table {
    public String value() default "";
}
