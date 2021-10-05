package ru.project.ticketsearchassistantbot.configs;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import ru.project.ticketsearchassistantbot.TelegramBot;
import ru.project.ticketsearchassistantbot.api.TelegramFacade;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;

    @Bean
    public TelegramBot telegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions defaultBotOptions = ApiContext
                .getInstance(DefaultBotOptions.class);

        defaultBotOptions.setProxyType(proxyType);
        defaultBotOptions.setProxyHost(proxyHost);
        defaultBotOptions.setProxyPort(proxyPort);

        TelegramBot telegramBot = new TelegramBot(defaultBotOptions, telegramFacade);
        telegramBot.setBotUserName(botUserName);
        telegramBot.setBotToken(botToken);
        telegramBot.setWebHookPath(webHookPath);

        return telegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
