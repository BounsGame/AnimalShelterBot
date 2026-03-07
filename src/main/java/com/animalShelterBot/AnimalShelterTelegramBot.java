package com.animalShelterBot;

import com.animalShelterBot.service.StartHandlerService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnimalShelterTelegramBot {
    private final TelegramBot telegramBot;

    @Value("${bot.token}")
    private String botToken;

    @Resource
    private StartHandlerService startHandlerService;

    public AnimalShelterTelegramBot(@Value("${bot.token}") String botToken) {
        this.telegramBot = new TelegramBot(botToken);
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    // Основной обработчик
    private void processUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            long chatId = update.message().chat().id();
            String messageText = update.message().text();
            if ("/start".equals(messageText)) {
                startHandlerService.handleStart(chatId);
            }
        }
    }
}
