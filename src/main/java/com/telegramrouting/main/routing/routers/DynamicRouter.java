package com.telegramrouting.main.routing.routers;

public interface DynamicRouter extends Router {
    void register(String command, HandlerMethodInfo handlerMethodInfo);
    void deregister(String command);
}
