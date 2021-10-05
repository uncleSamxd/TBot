package ru.project.ticketsearchassistantbot.api;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {
    SendMessage handle(Message message);

    BotApiMethod<?> handle(CallbackQuery buttonQuery, String callbackData);

    BotState getHandlerName();
}
