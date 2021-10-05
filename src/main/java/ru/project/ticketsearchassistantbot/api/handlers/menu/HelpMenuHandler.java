package ru.project.ticketsearchassistantbot.api.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.service.MainMenuService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

@Component
public class HelpMenuHandler implements InputMessageHandler {

    private final MainMenuService mainMenuService;
    private final ReplyMessagesService replyMessagesService;

    public HelpMenuHandler(MainMenuService mainMenuService, ReplyMessagesService replyMessagesService) {
        this.mainMenuService = mainMenuService;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                replyMessagesService.getReplyText("reply.showHelpMenu", Emojis.BOT_FACE));
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery, String callbackData) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU_HANDLER;
    }
}
