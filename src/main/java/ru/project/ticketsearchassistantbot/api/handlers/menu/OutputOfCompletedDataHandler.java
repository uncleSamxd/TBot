package ru.project.ticketsearchassistantbot.api.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.models.UserProfileData;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

@Component
public class OutputOfCompletedDataHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final ReplyMessagesService replyMessagesService;

    public OutputOfCompletedDataHandler(UserDataCache userDataCache, ReplyMessagesService replyMessagesService) {
        this.userDataCache = userDataCache;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = message.getFrom().getId();
        final UserProfileData profileData = userDataCache.getUserProfileData(userId);

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU_HANDLER);

        if (profileData.getDepartureCity() == null || profileData.getDestinationCity() == null || profileData.getDateDepart() == null) {
            return new SendMessage(message.getChatId(), replyMessagesService.getReplyText("reply.sayTheDataIsNotFilledIn",
                    Emojis.BOT_FACE, Emojis.WRONG));
        } else {
            return new SendMessage(message.getChatId(), profileData.toString());
        }
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery, String callbackData) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.OUTPUT_OF_COMPLETED_DATA_HANDLER;
    }
}
