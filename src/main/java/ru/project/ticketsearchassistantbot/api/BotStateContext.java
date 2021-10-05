package ru.project.ticketsearchassistantbot.api;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {

    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE_HANDLER);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_DEPARTURE_STATION:
            case ASK_ARRIVAL_STATION:
            case ASK_DEPARTURE_DATE:
            case FILLING_PROFILE_HANDLER:
                return true;
            default:
                return false;
        }
    }

    public BotApiMethod<?> handleCallbackQuery(BotState currentState, CallbackQuery buttonQuery, String callbackData) {
        InputMessageHandler currentMessageHandler = findCallbackHandler(currentState);
        return currentMessageHandler.handle(buttonQuery, callbackData);
    }

    private InputMessageHandler findCallbackHandler(BotState currentState) {
        if (isFillingCalendarState(currentState)) {
            return messageHandlers.get(BotState.FILLING_IN_THE_CALENDAR_HANDLER);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingCalendarState(BotState currentState) {
        switch (currentState) {
            case FILLING_IN_THE_CALENDAR_HANDLER:
            case CALENDAR_FILLED:
            case JANUARY:
            case FEBRUARY:
            case MARCH:
            case APRIL:
            case MAY:
            case JUNE:
            case JULY:
            case AUGUST:
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
            case DECEMBER:
                return true;
            default:
                return false;
        }
    }
}





