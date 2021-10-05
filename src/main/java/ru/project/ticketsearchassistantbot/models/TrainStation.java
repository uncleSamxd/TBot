package ru.project.ticketsearchassistantbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TrainStation {
    @JsonProperty(value = "n")
    private String stationName;
    @JsonProperty(value = "c")
    private int stationCode;
}
