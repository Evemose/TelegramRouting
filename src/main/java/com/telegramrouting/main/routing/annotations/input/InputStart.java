package com.telegramrouting.main.routing.annotations.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InputStart {
    /**
     * Name of the method that will be called when the input is received
     * @return name of the method
     */
    String handlerMethodName();
}
