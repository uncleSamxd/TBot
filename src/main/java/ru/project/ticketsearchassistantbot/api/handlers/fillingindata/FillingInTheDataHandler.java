package ru.project.ticketsearchassistantbot.api.handlers.fillingindata;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.models.UserProfileData;
import ru.project.ticketsearchassistantbot.service.InlineMessageButtonsService;
import ru.project.ticketsearchassistantbot.service.ParsingTicketService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.util.Date;

@Data
@Slf4j
@Component
public class FillingInTheDataHandler implements InputMessageHandler {

    private UserDataCache userDataCache;
    private ReplyMessagesService replyMessagesService;
    private final ParsingTicketService parsingTicketService;
    private String departureStation;
    private String arrivalStation;
    private int departureStationCode;
    private int arrivalStationCode;
    private Date dateDepart;
    private InlineMessageButtonsService inlineMessageButtonsService;

    public FillingInTheDataHandler(UserDataCache userDataCache, ReplyMessagesService replyMessagesService,
                                   ParsingTicketService parsingTicketService,
                                   InlineMessageButtonsService inlineMessageButtonsService) {
        this.userDataCache = userDataCache;
        this.replyMessagesService = replyMessagesService;
        this.parsingTicketService = parsingTicketService;
        this.inlineMessageButtonsService = inlineMessageButtonsService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE_HANDLER)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_DEPARTURE_STATION);
        }
        return processUsersInput(message);
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery, String callbackData) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE_HANDLER;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_DEPARTURE_STATION)) {
            replyToUser = replyMessagesService.getReplyMessage(chatId,
                    "reply.askTheCityOfDeparture", Emojis.BOT_FACE, Emojis.CITY_SUNRISE);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ARRIVAL_STATION);
        }

        if (botState.equals(BotState.ASK_ARRIVAL_STATION)) {
            departureStationCode = parsingTicketService.getStationCode(usersAnswer);

            if (departureStationCode == -1) {
                return replyMessagesService.getWarningReplyMessage(chatId,
                        replyMessagesService.getReplyText("reply.sayStationNotFound", Emojis.BOT_FACE));
            }

            profileData.setDepartureCity(usersAnswer);
            replyToUser = replyMessagesService.getReplyMessage(chatId,
                    "reply.askArrivalCity", Emojis.BOT_FACE, Emojis.CITY_SCAPE);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DEPARTURE_DATE);
        }

        if (botState.equals(BotState.ASK_DEPARTURE_DATE)) {
            arrivalStationCode = parsingTicketService.getStationCode(usersAnswer);

            if (arrivalStationCode == -1) {
                return replyMessagesService.getWarningReplyMessage(chatId,
                        replyMessagesService.getReplyText("reply.sayStationNotFound", Emojis.BOT_FACE));
            }

            if (arrivalStationCode == departureStationCode) {
                return replyMessagesService.getWarningReplyMessage(chatId,
                        replyMessagesService.getReplyText("reply.sayIdenticalStations", Emojis.BOT_FACE));
            }

            profileData.setDestinationCity(usersAnswer);
            replyToUser = replyMessagesService.getReplyMessage(chatId,
                    "reply.sayClickOnTheButtonCalendar", Emojis.BOT_FACE, Emojis.CALENDAR);
            replyToUser.setReplyMarkup(inlineMessageButtonsService.getInlineMessageButtons("Календарь", "calendar"));
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_HELP_MENU_HANDLER);
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }
}

