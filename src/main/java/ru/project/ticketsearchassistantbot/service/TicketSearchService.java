package ru.project.ticketsearchassistantbot.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import ru.project.ticketsearchassistantbot.models.Car;
import ru.project.ticketsearchassistantbot.models.Train;

import java.io.*;
import java.util.*;

@Service
public class TicketSearchService {

    private final List<Train> trainList = new ArrayList<>();

    public List<Train> getTrainList(String fromCity, String codeCityFrom, String toCity, String codeCityTo, String date) {

        //параметры для запроса
        fromCity = fromCity.toUpperCase();
        toCity = toCity.toUpperCase();

        //первый запрос, получаем rid
        Connection.Response res = null;
        Document documentFirst;
        String jsonStringDocumentFirst = null;
        String linkFirst = "https://pass.rzd.ru/timetable/public/ru?STRUCTURE_ID=735&layer_id=5371&dir=0&tfl=3&checkSeats=0&st0=" + fromCity + "&code0=" + codeCityFrom + "&st1=" + toCity + "&code1=" + codeCityTo + "&dt0=" + date + "";
        try {
            res = Jsoup.connect(linkFirst).method(Connection.Method.GET).execute();
            documentFirst = res.parse();
            jsonStringDocumentFirst = documentFirst.body().text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Получение из первого запроса RID и COOKIE
        Object obj;
        String rid = null;
        String sessionId = null;
        try {
            obj = new JSONParser().parse(jsonStringDocumentFirst);
            JSONObject ridJSON = (JSONObject) obj;
            rid = String.valueOf(ridJSON.get("rid")); //уникальный ключ доступа REQUEST_ID
            if (res != null) {
                sessionId = res.cookie("JSESSIONID"); //достаем cookie
            }
            Thread.sleep(2000);

        } catch (ParseException | InterruptedException e) {
            e.printStackTrace();
        }

        //второй запрос, получаем данные
        String jsonStringDocumentSecond = null;
        String linkSecond = "https://pass.rzd.ru/timetable/public/ru?layer_id=5371&rid=" + rid + "";
        Document documentSecond;
        try {
            documentSecond = Jsoup.connect(linkSecond).cookie("JSESSIONID", sessionId).get();
            jsonStringDocumentSecond = documentSecond.body().text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //достаем из ответа второго запроса нужные данные(билеты, время отправления и тд)
        Object objectJsonSecondDocument;
        try {
            objectJsonSecondDocument = new JSONParser().parse(jsonStringDocumentSecond);
            JSONObject getObjectJson = (JSONObject) objectJsonSecondDocument;
            JSONArray tp = (JSONArray) getObjectJson.get("tp");

            for (Object o : tp) {
                JSONObject tpObj = (JSONObject) o;
                JSONArray list = (JSONArray) tpObj.get("list");
                Iterator listIter = list.iterator();

                int countTrains = 1;
                while (listIter.hasNext()) {

                    JSONObject listObj = (JSONObject) listIter.next();
                    JSONArray cars = (JSONArray) listObj.get("cars");
                    Iterator carsIter = cars.iterator();

                    List<Car> listOfCars = new ArrayList<>();

                    while (carsIter.hasNext()) {
                        JSONObject carsObj = (JSONObject) carsIter.next();
                        if (carsObj.get("typeLoc").equals("Багажное купе")) continue;
                        listOfCars.add(new Car((String) carsObj.get("typeLoc"), (String) carsObj.get("tariff"), (String) carsObj.get("freeSeats")));
                    }
                    trainList.add(new Train(String.valueOf(countTrains), (String) listObj.get("station0"),
                            (String) listObj.get("date0"), (String) listObj.get("time0"),
                            (String) listObj.get("station1"), (String) listObj.get("date1"),
                            (String) listObj.get("time1"), listOfCars));
                    countTrains++;
                }
            }
        } catch (ParseException e) {
            System.out.println("Нет билетов!");
        }

        return trainList;
    }

    public void clearTrainList() {
        trainList.clear();
    }
}
