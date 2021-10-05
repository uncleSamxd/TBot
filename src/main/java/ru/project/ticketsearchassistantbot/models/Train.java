package ru.project.ticketsearchassistantbot.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Train implements Serializable {

	private static final long serialVersionUID = 1L;

	public Train(String id, String departureStation, String dateDeparture, String timeDeparture, String arrivalStation, String dateArrival, String timeArrival, List<Car> cars) {
		this.id = id;
		this.departureStation = departureStation;
		this.dateDeparture = dateDeparture;
		this.timeDeparture = timeDeparture;
		this.arrivalStation = arrivalStation;
		this.dateArrival = dateArrival;
		this.timeArrival = timeArrival;
		this.cars = cars;
	}

	String id;
	String departureStation;
	String dateDeparture;
	String timeDeparture;
	String arrivalStation;
	String dateArrival;
	String timeArrival;
	List<Car> cars;
}
