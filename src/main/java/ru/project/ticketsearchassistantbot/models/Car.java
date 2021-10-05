package ru.project.ticketsearchassistantbot.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    public Car(String typeTicketOfCar, String minimumPrice, String availableSeats) {
        this.typeTicketOfCar = typeTicketOfCar;
        this.minimumPrice = minimumPrice;
        this.availableSeats = availableSeats;
    }

    String typeTicketOfCar;
    String minimumPrice;
    String availableSeats;
}
