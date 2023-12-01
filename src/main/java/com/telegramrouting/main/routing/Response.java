package com.telegramrouting.main.routing;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Builder
public record Response(BotApiMethodMessage... messages) {
}
