package com.telegramrouting.main.routing;

import com.telegramrouting.main.routing.routers.callback.CallbackRouter;
import com.telegramrouting.main.routing.routers.Router;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Component
public class MainUpdateHandler extends TelegramLongPollingBot {

    private final String username;
    private final Router commandRouter;
    private final CallbackRouter callbackRouter;

    @Override
    public void onUpdateReceived(Update update) {
        Optional<Response> response;
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                response = commandRouter.route(update);
            } else {
                response = Optional.empty();
            }
        } else {
            response = callbackRouter.route(update);
        }
        response.ifPresent(res -> {
            for (var message : res.messages()) {
                try {
                    var method = message.getClass().getMethod("getChatId");
                    if (method.invoke(message) == null) {
                        method = message.getClass().getMethod("setChatId", Long.class);
                        var chatId = update.hasCallbackQuery()
                                ? update.getCallbackQuery().getMessage().getChatId()
                                : update.getMessage().getChatId();
                        method.invoke(message, chatId);
                    }
                    execute(message);
                } catch (TelegramApiException
                         | NoSuchMethodException
                         | IllegalAccessException
                         | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    public MainUpdateHandler(@Value("${telegram.bot.username}") String username,
                             @Value("${telegram.bot.token}") String token,
                             @Qualifier("commandRouter") Router commandRouter, CallbackRouter callbackRouter) {
        super(token);
        this.username = username;
        this.commandRouter = commandRouter;
        this.callbackRouter = callbackRouter;
    }

    @PostConstruct
    private void register() throws TelegramApiException {
        new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
        var msg = new SendMessage();
    }

}
