package ru.project.ticketsearchassistantbot.api;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.project.ticketsearchassistantbot.TelegramBot;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.service.CallbackService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

@Component
@Slf4j
public class TelegramFacade {

    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final CallbackService callbackService;
    private final TelegramBot telegramBot;
    private final ReplyMessagesService replyMessagesService;

    public TelegramFacade(ReplyMessagesService replyMessagesService,
                          @Lazy TelegramBot telegramBot,
                          BotStateContext botStateContext,
                          UserDataCache userDataCache,
                          CallbackService callbackService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.callbackService = callbackService;
        this.telegramBot = telegramBot;
        this.replyMessagesService = replyMessagesService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return callbackService.processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                telegramBot.sendPhoto(chatId, replyMessagesService.getReplyText("reply.sayHello", Emojis.BOT_FACE, Emojis.TRAIN), "static/images/logo.jpg");
                botState = BotState.START_OF_TICKET_SEARCH_HANDLER;
                break;
            case "Заполнение параметров для поиска":
                botState = BotState.FILLING_PROFILE_HANDLER;
                break;
            case "Данные о запросе":
                botState = BotState.OUTPUT_OF_COMPLETED_DATA_HANDLER;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU_HANDLER;
                break;
            case "Поиск билетов":
                botState = BotState.SHOW_FOUND_TRAINS_HANDLER;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }
}