package ru.project.ticketsearchassistantbot.api.handlers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.service.InlineMessageButtonsService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

@Slf4j
@Component
public class StartOfSearchHandler implements InputMessageHandler {

    private final ReplyMessagesService replyMessagesService;
    private final InlineMessageButtonsService inlineMessageButtonsService;

    public StartOfSearchHandler(ReplyMessagesService replyMessagesService,
                                InlineMessageButtonsService inlineMessageButtonsService) {
        this.replyMessagesService = replyMessagesService;
        this.inlineMessageButtonsService = inlineMessageButtonsService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery, String callbackData) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START_OF_TICKET_SEARCH_HANDLER;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = replyMessagesService.getReplyMessage(chatId,
                "reply.askTicketSearchSuggestion", Emojis.BOT_FACE, Emojis.TRAIN);
        replyToUser.setReplyMarkup(inlineMessageButtonsService.getInlineMessageButtons());

        return replyToUser;
    }
}



