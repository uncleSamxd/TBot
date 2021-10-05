package ru.project.ticketsearchassistantbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InlineMessageButtonsService {

    public InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton().setText("Да");
        InlineKeyboardButton buttonNo = new InlineKeyboardButton().setText("Нет, спасибо.");

        buttonYes.setCallbackData("buttonYes");
        buttonNo.setCallbackData("buttonNo");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonYes);
        keyboardButtonsRow1.add(buttonNo);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageButtons(String button, String callbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton calendar = new InlineKeyboardButton().setText(button).setCallbackData(callbackData);

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(calendar);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageButtons(int M, int Y) {
        String[] months = {
                "",
                "Январь", "Февраль", "Март",
                "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь",
                "Октябрь", "Ноябрь", "Декабрь"
        };

        int[] days = {
                0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        if (M == 2 && isLeapYear(Y)) days[M] = 29;

        int d = day(M, 0, Y);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstLine = new ArrayList<>();
        firstLine.add(new InlineKeyboardButton().setText(months[M] + " " + Y).setCallbackData(months[M]));

        List<InlineKeyboardButton> secondLine = new ArrayList<>();
        secondLine.add(new InlineKeyboardButton().setText("Пн").setCallbackData("пн"));
        secondLine.add(new InlineKeyboardButton().setText("Вт").setCallbackData("вт"));
        secondLine.add(new InlineKeyboardButton().setText("Ср").setCallbackData("ср"));
        secondLine.add(new InlineKeyboardButton().setText("Чт").setCallbackData("чт"));
        secondLine.add(new InlineKeyboardButton().setText("Пт").setCallbackData("пт"));
        secondLine.add(new InlineKeyboardButton().setText("Сб").setCallbackData("сб"));
        secondLine.add(new InlineKeyboardButton().setText("Вс").setCallbackData("вс"));

        List<InlineKeyboardButton> thirdLine = new ArrayList<>();
        List<InlineKeyboardButton> fourthLine = new ArrayList<>();
        List<InlineKeyboardButton> fifthLine = new ArrayList<>();
        List<InlineKeyboardButton> sixthLine = new ArrayList<>();
        List<InlineKeyboardButton> seventhLine = new ArrayList<>();
        List<InlineKeyboardButton> eighthLine = new ArrayList<>();

        Map<Integer, List<InlineKeyboardButton>> position = new HashMap<>();
        position.put(1, thirdLine);
        position.put(2, fourthLine);
        position.put(3, fifthLine);
        position.put(4, sixthLine);
        position.put(5, seventhLine);
        position.put(6, eighthLine);


        for (int i = 0; i < d; i++) {
            thirdLine.add(new InlineKeyboardButton().setText(" ").setCallbackData(" "));
        }

        int j = 1;
        List<InlineKeyboardButton> temp;
        temp = position.get(1);
        for (int i = 1; i <= days[M]; i++) {
            temp.add(new InlineKeyboardButton().setText(String.valueOf(i)).setCallbackData(i + "." + M + "." + Y));
            if (i != days[M]) {
                if (((i + d) % 7 == 0)) {
                    j++;
                    temp = position.get(j);
                }
            }
        }

        if (j == 6) {
            for (int i = 1; i <= 42 - (days[M] + d); i++) {
                temp.add(new InlineKeyboardButton().setText(" ").setCallbackData(" "));
            }
        }

        if (j == 5) {
            for (int i = 1; i <= 35 - (days[M] + d); i++) {
                temp.add(new InlineKeyboardButton().setText(" ").setCallbackData(" "));
            }
        }

        if (j == 4) {
            for (int i = 1; i <= 28 - (days[M] + d); i++) {
                temp.add(new InlineKeyboardButton().setText(" ").setCallbackData(" "));
            }
        }

        List<InlineKeyboardButton> lastLine = new ArrayList<>();
        lastLine.add(new InlineKeyboardButton().setText("<").setCallbackData("left"));
        lastLine.add(new InlineKeyboardButton().setText(">").setCallbackData("right"));

        rowsInline.add(firstLine);
        rowsInline.add(secondLine);
        rowsInline.add(thirdLine);
        rowsInline.add(fourthLine);
        rowsInline.add(fifthLine);
        rowsInline.add(sixthLine);
        rowsInline.add(seventhLine);
        rowsInline.add(eighthLine);
        rowsInline.add(lastLine);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public static int day(int M, int D, int Y) {
        int y = Y - (14 - M) / 12;
        int x = y + y / 4 - y / 100 + y / 400;
        int m = M + 12 * ((14 - M) / 12) - 2;
        return (D + x + (31 * m) / 12) % 7;
    }

    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0) && (year % 100 != 0)) return true;
        return year % 400 == 0;
    }
}
