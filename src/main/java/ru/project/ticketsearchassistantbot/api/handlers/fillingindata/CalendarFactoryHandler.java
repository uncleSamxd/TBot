package ru.project.ticketsearchassistantbot.api.handlers.fillingindata;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.models.UserProfileData;
import ru.project.ticketsearchassistantbot.service.InlineMessageButtonsService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.toIntExact;

@Slf4j
@Component
public class CalendarFactoryHandler implements InputMessageHandler {

    private final ReplyMessagesService replyMessagesService;
    private final UserDataCache userDataCache;
    private final InlineMessageButtonsService inlineMessageButtonsService;

    private final Calendar calendar = new GregorianCalendar();
    private final Calendar variableCalendar = new GregorianCalendar();
    private final Date date = calendar.getTime();

    private final Locale locale = Locale.US;
    private final DateFormat currentMonth = new SimpleDateFormat("MMMM", locale);
    private final DateFormat month = new SimpleDateFormat("M", locale);
    private final DateFormat currentYear = new SimpleDateFormat("yyyy", locale);

    public CalendarFactoryHandler(ReplyMessagesService replyMessagesService,
                                  UserDataCache userDataCache,
                                  InlineMessageButtonsService inlineMessageButtonsService) {
        this.replyMessagesService = replyMessagesService;
        this.userDataCache = userDataCache;
        this.inlineMessageButtonsService = inlineMessageButtonsService;
    }

    @Override
    public SendMessage handle(Message message) {
        return null;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery buttonQuery, String callbackData) {
        if (userDataCache.getUsersCurrentBotState(buttonQuery.getFrom().getId()).equals(BotState.FILLING_IN_THE_CALENDAR_HANDLER)) {
            userDataCache.setUsersCurrentBotState(buttonQuery.getFrom().getId(), BotState.valueOf(currentMonth.format(date).toUpperCase(locale)));
        }
        return processUsersInput(buttonQuery, callbackData);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_IN_THE_CALENDAR_HANDLER;
    }

    private BotApiMethod<?> processUsersInput(CallbackQuery callbackQuery, String callbackData) {
        long chatId = callbackQuery.getMessage().getChatId();
        int userId = callbackQuery.getFrom().getId();
        long message_id = callbackQuery.getMessage().getMessageId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        EditMessageText new_message = null;

        for (int i = 1; i <= 12; i++) {
            variableCalendar.set(Calendar.MONTH, i);
            Date variableDate = variableCalendar.getTime();
            if (botState.equals(BotState.valueOf(currentMonth.format(variableDate).toUpperCase(locale)))) {
                new_message = new EditMessageText().setChatId(chatId).setMessageId(toIntExact(message_id)).setText(replyMessagesService.getReplyText("reply.askTheDateOfDeparture", Emojis.BOT_FACE))
                        .setReplyMarkup(inlineMessageButtonsService.getInlineMessageButtons(Integer.parseInt(month.format(variableDate)), Integer.parseInt(currentYear.format(date))));
            }
        }

        if (botState.equals(BotState.CALENDAR_FILLED)) {
            if (callbackData != null) {
                Date dateDepart;
                try {
                    dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(callbackData);
                } catch (ParseException e) {
                    return replyMessagesService.getWarningReplyMessage(chatId, "Неверный формат даты");
                }

                final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                String dateDepartStr = dateFormatter.format(dateDepart);

                profileData.setDateDepart(dateDepartStr);
                new_message = new EditMessageText().setChatId(chatId).setMessageId(toIntExact(message_id))
                        .setText(replyMessagesService.getReplyText("reply.sayClickOnTheButtonMenu", Emojis.BOT_FACE))
                        .setReplyMarkup(inlineMessageButtonsService.getInlineMessageButtons("Меню", "menu"));
            } else {
                new_message = new EditMessageText().setChatId(chatId).setMessageId(toIntExact(message_id)).setText("Ошибка");
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU_HANDLER);
        }
        userDataCache.saveUserProfileData(userId, profileData);

        return new_message;
    }
}
