package com.telegramrouting.main.routing.routers.command;

import com.telegramrouting.main.routing.annotations.command.CommandHandler;
import com.telegramrouting.main.routing.annotations.command.CommandMapping;
import com.telegramrouting.main.routing.routers.ScanningMethodRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Slf4j
public final class CommandRouter extends ScanningMethodRouter {
    private CommandRouter(WebApplicationContext applicationContext) {
        super(applicationContext, CommandHandler.class, CommandMapping.class);
    }

    @Override
    protected String getRequestKey(Update update) {
        return update.getMessage().getText();
    }
}
