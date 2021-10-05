package ru.project.ticketsearchassistantbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emojis {

    BOT_FACE(EmojiParser.parseToUnicode(":bot_face:")),
    TRAIN(EmojiParser.parseToUnicode(":steam_locomotive:")),
    CITY_SUNRISE(EmojiParser.parseToUnicode(":city_sunrise:")),
    CITY_SCAPE(EmojiParser.parseToUnicode(":cityscape:")),
    CALENDAR(EmojiParser.parseToUnicode(":calendar:")),
    WRONG(EmojiParser.parseToUnicode(":no_entry_sign:")),
    ARROW_RIGHT(EmojiParser.parseToUnicode(":arrow_right:")),
    ALARM_CLOCK(EmojiParser.parseToUnicode(":alarm_clock:")),
    FREE(EmojiParser.parseToUnicode(":free:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
