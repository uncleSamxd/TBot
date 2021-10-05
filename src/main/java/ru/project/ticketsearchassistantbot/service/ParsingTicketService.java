package ru.project.ticketsearchassistantbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.project.ticketsearchassistantbot.models.TrainStation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ParsingTicketService {

    @Value("${stationcodeservice.requesttemplate}")
    private String stationCodeRequestTemplate;
    private RestTemplate restTemplate = new RestTemplate();


    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = getOptionalStationCode(stationNameParam);
        if (stationCodeOptional.isPresent()) return stationCodeOptional.get();
        processStationCodeRequest(stationNameParam);

        return getOptionalStationCode(stationNameParam).orElse(-1);
    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart) {
        ResponseEntity<TrainStation[]> response = restTemplate.getForEntity(stationCodeRequestTemplate,
                TrainStation[].class, stationNamePart);
        TrainStation[] stations = response.getBody();
        if (stations == null) {
            return Optional.empty();
        }

        for (TrainStation station : stations) {
            addStationToCache(station.getStationName(), station.getStationCode());
        }

        return Optional.of(stations);
    }

    private Map<String, Integer> stationCodeCache = new HashMap<>();

    public Optional<String> getStationName(String stationNameParam) {
        return stationCodeCache.keySet().stream().filter(stationName -> stationName.equals(stationNameParam)).findFirst();
    }

    public Optional<Integer> getOptionalStationCode(String stationNameParam) {
        return Optional.ofNullable(stationCodeCache.get(stationNameParam));
    }

    public void addStationToCache(String stationName, int stationCode) {
        stationCodeCache.put(stationName, stationCode);
    }
}
