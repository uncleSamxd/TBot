package ru.project.ticketsearchassistantbot.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import ru.project.ticketsearchassistantbot.TelegramBot;
import ru.project.ticketsearchassistantbot.models.Car;
import ru.project.ticketsearchassistantbot.models.Train;
import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.util.List;

@Service
public class TrainTicketsInfo {

    private final ReplyMessagesService messagesService;
    private final TelegramBot telegramBot;

    public TrainTicketsInfo(ReplyMessagesService messagesService, @Lazy TelegramBot telegramBot) {
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        StringBuilder strCars = new StringBuilder("\n");
        for (Train train : trainsList) {
            List<Car> cars = train.getCars();
            for (Car car : cars) {
                strCars.append(Emojis.ARROW_RIGHT + " " + car.getTypeTicketOfCar()).append(": ").append(car.getAvailableSeats()).append(" (от ").append(car.getMinimumPrice()).append(" руб.)").append("\n");
            }
            String trainTicketsInfoMessage = messagesService.getReplyText("reply.trainSearch.trainInfo",
                    train.getId(), train.getDepartureStation(), train.getDateDeparture(), train.getTimeDeparture(), train.getArrivalStation(),
                    train.getDateArrival(), train.getTimeArrival(), strCars.toString(), Emojis.TRAIN, Emojis.CALENDAR, Emojis.ALARM_CLOCK, Emojis.FREE, Emojis.CITY_SUNRISE, Emojis.CITY_SCAPE);
            telegramBot.sendMessage(chatId, trainTicketsInfoMessage);
            strCars = new StringBuilder("\n");
        }
    }
}
