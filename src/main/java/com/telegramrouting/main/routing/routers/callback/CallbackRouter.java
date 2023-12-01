package com.telegramrouting.main.routing.routers.callback;

import com.telegramrouting.main.routing.annotations.callback.CallbackHandler;
import com.telegramrouting.main.routing.annotations.callback.CallbackMapping;
import com.telegramrouting.main.routing.routers.HandlerMethodInfo;
import com.telegramrouting.main.routing.routers.ScanningMethodRouter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public final class CallbackRouter extends ScanningMethodRouter {
    private CallbackRouter(WebApplicationContext applicationContext) {
        super(applicationContext, CallbackHandler.class, CallbackMapping.class);
    }

    @Override
    protected HandlerMethodInfo getMethodHandler(String callback) {
        return routes.entrySet().stream()
                .filter(entry -> callback.matches(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected String getRequestKey(Update update) {
        return update.getCallbackQuery().getData();
    }
}
