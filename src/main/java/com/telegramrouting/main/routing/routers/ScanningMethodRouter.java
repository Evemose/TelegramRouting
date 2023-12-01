package com.telegramrouting.main.routing.routers;

import com.telegramrouting.main.routing.Response;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.annotation.Annotation;
import java.util.Objects;

public abstract class ScanningMethodRouter extends AbstractMethodRouter {

    private final Class<? extends Annotation> classAnnotation;
    private final Class<? extends Annotation> methodAnnotation;

    protected ScanningMethodRouter(WebApplicationContext applicationContext, Class<? extends Annotation> classAnnotation, Class<? extends Annotation> methodAnnotation) {
        super(applicationContext);
        this.classAnnotation = classAnnotation;
        try {
            methodAnnotation.getDeclaredMethod("value");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method annotation must have value field");
        }
        this.methodAnnotation = methodAnnotation;
    }

    @PostConstruct
    private void scan() {
        var handlers = applicationContext.getBeansWithAnnotation(classAnnotation);
        for (var handler : handlers.values()) {
            for (var method : ReflectionUtils.getAllDeclaredMethods(handler.getClass())) {
                var annotation = AnnotationUtils.findAnnotation(method, methodAnnotation);
                if (annotation != null) {
                    if (method.getParameterCount() > 1
                            || method.getParameterCount() == 1 && !method.getParameterTypes()[0].equals(Update.class)
                            || !method.getReturnType().isAssignableFrom(Response.class)) {
                        throw new RuntimeException("Command handler method must have exactly one parameter");
                    }
                    var command = Objects.requireNonNull(AnnotationUtils.getValue(annotation, "value")).toString();
                    method.setAccessible(true);
                    routes.put(command, new HandlerMethodInfo(method, handler, method.getParameterCount()));
                }
            }
        }
    }
}
