package ru.project.ticketsearchassistantbot.api.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.api.InputMessageHandler;
import ru.project.ticketsearchassistantbot.cache.UserDataCache;
import ru.project.ticketsearchassistantbot.models.Train;
import ru.project.ticketsearchassistantbot.models.UserProfileData;
import ru.project.ticketsearchassistantbot.service.ParsingTicketService;
import ru.project.ticketsearchassistantbot.service.ReplyMessagesService;
import ru.project.ticketsearchassistantbot.service.TicketSearchService;
import ru.project.ticketsearchassistantbot.service.TrainTicketsInfo;
import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.util.List;

@Component
public class ShowFoundTrainsHandler implements InputMessageHandler {

    private final UserDataCache userDataCache;
    private final TicketSearchService ticketSearchService;
    private final ParsingTicketService parsingTicketService;
    private final TrainTicketsInfo trainTicketsInfo;
    private final ReplyMessagesService replyMessagesService;

    public ShowFoundTrainsHandler(UserDataCache userDataCache,
                                  TicketSearchService ticketSearchService,
                                  ParsingTicketService parsingTicketService,
                                  TrainTicketsInfo trainTicketsInfo,
                                  ReplyMessagesService replyMessagesService) {
        this.userDataCache = userDataCache;
        this.ticketSearchService = ticketSearchService;
        this.parsingTicketService = parsingTicketService;
        this.trainTicketsInfo = trainTicketsInfo;
        this.replyMessagesService = replyMessagesService;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        SendMessage replyToUser;
        int userId = inputMessage.getFrom().getId();
        long chatId = inputMessage.getChatId();
        UserProfileData profileData = userDataCache.getUserProfileData(userId);

        if (profileData.getDepartureCity() == null || profileData.getDestinationCity() == null || profileData.getDateDepart() == null) {
            replyToUser = replyMessagesService.getReplyMessage(chatId, "reply.sayTheDataIsNotFilledIn",
                    Emojis.BOT_FACE, Emojis.WRONG);
        } else {
            String departureStation = profileData.getDepartureCity();
            String dateDeparture = profileData.getDateDepart();
            int departCode = parsingTicketService.getStationCode(departureStation);
            String arrivalStation = profileData.getDestinationCity();
            int arrivalCode = parsingTicketService.getStationCode(arrivalStation);
            List<Train> trainList = ticketSearchService.getTrainList(departureStation, String.valueOf(departCode), arrivalStation, String.valueOf(arrivalCode), dateDeparture);
            if (trainList.size() == 0) {
                replyToUser = replyMessagesService.getReplyMessage(chatId, "reply.sayTheTrainDoesn'tRun", Emojis.BOT_FACE, Emojis.TRAIN);
            } else {
                trainTicketsInfo.sendTrainTicketsInfo(chatId, trainList);
                ticketSearchService.clearTrainList();
                replyToUser = replyMessagesService.getReplyMessage(chatId, "reply.theEndSmile", Emojis.BOT_FACE);
            }
        }

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU_HANDLER);

        return replyToUser;
    }

    @Override
    public SendMessage handle(CallbackQuery buttonQuery, String callbackData) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_FOUND_TRAINS_HANDLER;
    }
}
