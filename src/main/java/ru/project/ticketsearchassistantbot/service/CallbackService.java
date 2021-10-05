package ru.project.ticketsearchassistantbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.BotStateContext;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@Service
public class CallbackService {

    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;
    private final ReplyMessagesService replyMessagesService;
    private final Calendar calendar = Calendar.getInstance();
    private final int currentMonth = calendar.get(Calendar.MONTH);
    private int count = currentMonth;
    private final Calendar callbackCalendar = new GregorianCalendar();
    private final Locale locale = Locale.US;
    private final DateFormat dateFormat = new SimpleDateFormat("MMMM", locale);

    public CallbackService(BotStateContext botStateContext,
                           UserDataCache userDataCache,
                           MainMenuService mainMenuService,
                           ReplyMessagesService replyMessagesService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.replyMessagesService = replyMessagesService;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, replyMessagesService.getReplyText("reply.showMainMenu", Emojis.BOT_FACE));

        try {
            if (Integer.parseInt(String.valueOf(buttonQuery.getData().charAt(0))) >= 1 && Integer.parseInt(String.valueOf(buttonQuery.getData().charAt(0))) <= 31) {
                userDataCache.setUsersCurrentBotState(userId, BotState.CALENDAR_FILLED);
                callBackAnswer = botStateContext.handleCallbackQuery(BotState.FILLING_IN_THE_CALENDAR_HANDLER, buttonQuery, buttonQuery.getData());
            } else if (buttonQuery.getData().equals(" ")) {
                System.out.println("Пустая кнопка");
            }
        } catch (NumberFormatException e) {

            switch (buttonQuery.getData()) {
                case "buttonYes":
                    callBackAnswer = new SendMessage(chatId, replyMessagesService.getReplyText("reply.askTheCityOfDeparture", Emojis.BOT_FACE, Emojis.CITY_SUNRISE));
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ARRIVAL_STATION);
                    break;
                case "buttonNo":
                    callBackAnswer = sendAnswerCallbackQuery(replyMessagesService.getReplyText("reply.sayComeBack", Emojis.BOT_FACE), buttonQuery);
                    break;
            }

            if (buttonQuery.getData().equals("calendar")) {
                userDataCache.setUsersCurrentBotState(userId, BotState.FILLING_IN_THE_CALENDAR_HANDLER);
                callBackAnswer = botStateContext.handleCallbackQuery(BotState.FILLING_IN_THE_CALENDAR_HANDLER, buttonQuery, "callbackData");
            }

            Date date;
            if (buttonQuery.getData().equals("right")) {
                count++;
                callbackCalendar.set(Calendar.MONTH, count);
                date = callbackCalendar.getTime();
                userDataCache.setUsersCurrentBotState(userId, BotState.valueOf(dateFormat.format(date).toUpperCase(locale)));
                callBackAnswer = botStateContext.handleCallbackQuery(BotState.FILLING_IN_THE_CALENDAR_HANDLER, buttonQuery, "callbackData");
            } else if (buttonQuery.getData().equals("left")) {
                count--;
                callbackCalendar.set(Calendar.MONTH, count);
                date = callbackCalendar.getTime();
                userDataCache.setUsersCurrentBotState(userId, BotState.valueOf(dateFormat.format(date).toUpperCase(locale)));
                callBackAnswer = botStateContext.handleCallbackQuery(BotState.FILLING_IN_THE_CALENDAR_HANDLER, buttonQuery, "callbackData");
            }

            if (buttonQuery.getData().equals(" ")) {
                callBackAnswer = sendAnswerCallbackQuery(replyMessagesService.getReplyText("reply.sayWrongButton", Emojis.BOT_FACE, Emojis.WRONG), buttonQuery);
            }
        }
        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(false);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
