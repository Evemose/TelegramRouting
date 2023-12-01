package com.telegramrouting.main.routing.routers.input;

import com.telegramrouting.main.routing.routers.DynamicRouter;
import com.telegramrouting.main.routing.routers.HandlerMethodInfo;
import com.telegramrouting.main.routing.annotations.input.InputStart;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Exchanger;

@Aspect
@Component
@RequiredArgsConstructor
public class InputAspect {

    private final DynamicRouter inputRouter;

    private final Set<String> chatIds = new HashSet<>();

    @SneakyThrows
    @After("@annotation(com.telegramrouting.main.routing.annotations.input.InputStart)")
    public void registerInputHandler(JoinPoint joinPoint) {
        var inputHandlerMethodName = ((MethodSignature)joinPoint.getSignature()).getMethod()
                .getAnnotation(InputStart.class)
                .handlerMethodName();
        var absolutePathRegex = "^[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*$";
        var update = (Update)joinPoint.getArgs()[0];
        Class<?> clazz;
        Method method;

        if (inputHandlerMethodName.matches(absolutePathRegex)) {
            var absolutePathSplit = inputHandlerMethodName.split("\\.");
            var className = String.join(".", Arrays.copyOfRange(absolutePathSplit, 0, absolutePathSplit.length - 2));
            var methodName = absolutePathSplit[absolutePathSplit.length - 1];
            clazz = Class.forName(className);
            method = clazz.getMethod(methodName, Exchanger.class);
        } else {
            clazz = joinPoint.getTarget().getClass();
            method = clazz.getMethod(inputHandlerMethodName, Exchanger.class);
        }

        inputRouter.register(update.getMessage().getChatId().toString(), new HandlerMethodInfo(method, clazz, 1));
    }

    @After("@annotation(com.telegramrouting.main.routing.annotations.input.InputHandler)")
    public void deregisterInputHandler() {
        inputRouter.deregister();
    }
}
