package com.telegramrouting.main.routing.routers.input;

import com.telegramrouting.main.routing.Response;
import com.telegramrouting.main.routing.routers.DynamicRouter;
import com.telegramrouting.main.routing.routers.HandlerMethodInfo;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class InputRouter implements DynamicRouter {
    @Override
    public void register(String command, HandlerMethodInfo handlerMethodInfo) {
        routes.put(command, handlerMethodInfo);
    }

    @Override
    public void deregister(String command) {
        routes.remove(command);
    }

    @Override
    public Optional<Response> route(Update update) {

        return Optional.empty();
    }
}
