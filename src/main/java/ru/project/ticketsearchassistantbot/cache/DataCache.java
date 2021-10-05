package ru.project.ticketsearchassistantbot.cache;

import ru.project.ticketsearchassistantbot.api.BotState;
import ru.project.ticketsearchassistantbot.models.UserProfileData;

public interface DataCache {

    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
