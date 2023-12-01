package com.telegramrouting.main.routing.routers;

import com.telegramrouting.main.routing.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface Router {
    Optional<Response> route(Update update);
}
