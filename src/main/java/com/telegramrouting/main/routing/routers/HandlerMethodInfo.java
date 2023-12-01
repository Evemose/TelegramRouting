package com.telegramrouting.main.routing.routers;

import com.telegramrouting.main.routing.Response;

import java.lang.reflect.Method;

public record HandlerMethodInfo(Method method, Object handler, int parameterCount) {
    public Response invoke(Object... args) {
        try {
            return (Response) method.invoke(handler, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
