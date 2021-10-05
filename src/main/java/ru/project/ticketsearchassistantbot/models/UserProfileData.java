package ru.project.ticketsearchassistantbot.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import ru.project.ticketsearchassistantbot.utils.Emojis;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String departureCity;
    String destinationCity;
    String dateDepart;


    @Override
    public String toString() {
        return String.format("%1$s Введенные параметры:%n%n%2$s Город отправления:%n%3$s %6$s%n%n%2$s Город назначения:%n%4$s %7$s%n%n%2$s Дата отправления:%n%5$s %8$s%n",
                Emojis.BOT_FACE, Emojis.ARROW_RIGHT, Emojis.CITY_SUNRISE, Emojis.CITY_SCAPE,
                Emojis.CALENDAR, getDepartureCity(), getDestinationCity(), getDateDepart());
    }
}
