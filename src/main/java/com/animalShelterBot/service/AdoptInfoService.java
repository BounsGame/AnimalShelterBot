package com.animalShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AdoptInfoService {

    @Resource
    private TelegramBot telegramBot;

    /** Как взять животное */
    public void handleAdoptInfo(long chatId) {
        String text = "🐾 *Как взять животное:*\n" +
                "1️⃣ Заполните анкету потенциального хозяина\n" +
                "2️⃣ Дождитесь звонка волонтёра\n" +
                "3️⃣ Приезжайте знакомиться с питомцем\n" +
                "4️⃣ Подпишите договор и заберите друга!";
        sendMessageWithMarkdown(chatId, text);
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }

    private void sendMessageWithMarkdown(long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        message.parseMode(ParseMode.Markdown);
        telegramBot.execute(message);
    }
}
