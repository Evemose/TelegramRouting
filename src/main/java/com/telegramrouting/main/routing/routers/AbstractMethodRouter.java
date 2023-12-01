package com.telegramrouting.main.routing.routers;

import com.telegramrouting.main.routing.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class AbstractMethodRouter implements Router {
    protected final Map<String, HandlerMethodInfo> routes;
    protected final WebApplicationContext applicationContext;
    protected AbstractMethodRouter(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        routes = new HashMap<>();
    }

    @Override
    public Optional<Response> route(Update update) {
        var command = getRequestKey(update);
        var methodHandler = getMethodHandler(command);
        if (methodHandler == null) {
            log.warn("Route for command {} not found", command);
            return Optional.empty();
        }
        try {
            log.info("Routing command {}", command);
            log.trace("Invoking method {} for command {}", methodHandler.method(), command);
            Response result;
            if (methodHandler.parameterCount() == 0) {
                result = methodHandler.invoke();
            } else {
                result = methodHandler.invoke(update);
            }
            return Optional.of(result);
        } catch (Exception e) {
            log.error("Error while invoking command handler", e);
            return Optional.empty();
        }
    }

    protected HandlerMethodInfo getMethodHandler(String command) {
        return routes.get(command);
    }

    protected abstract String getRequestKey(Update update);

}
