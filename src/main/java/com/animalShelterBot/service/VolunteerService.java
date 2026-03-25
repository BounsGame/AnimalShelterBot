package com.animalShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;

public class VolunteerService {

    private final TelegramBot telegramBot;

    private final UserSessionService userSessionService;

    private final ContactDataService contactDataService;

    @Value("${telegram.bot.volunteer-chat-id:123456789}")
    private String volunteerChatId;

    public VolunteerService(TelegramBot telegramBot, UserSessionService userSessionService, ContactDataService contactDataService) {
        this.telegramBot = telegramBot;
        this.userSessionService = userSessionService;
        this.contactDataService = contactDataService;
    }

    public void handleVolunteerCall(long chatId) {
        // 1. Подтверждение пользователю
        sendMessage(chatId, "✅ Волонтёр уже уведомлён! Ожидайте ответа в течение 5-10 минут.");

        // 2. Обновляем состояние
        userSessionService.setStateVolunteerCalled(chatId);

        // 3. Уведомление волонтёрам
        String alert = String.format("🆘 *Вызов волонтёра!*\n" + "👤 Пользователь: `%d`\n" + "⏰ Время: `%s`", chatId, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
        SendMessage alertMessage = new SendMessage(volunteerChatId, alert);
        alertMessage.parseMode(ParseMode.Markdown);

        try {
            telegramBot.execute(alertMessage);
        } catch (Exception e) {
            // Если не удалось отправить в админ-чат — логируем ошибку
            System.err.println("Не удалось отправить уведомление волонтёрам: " + e.getMessage());
        }
    }

    public void reportDateOverdue (Long userChatId){
        try {
            telegramBot.execute(new SendMessage(volunteerChatId, "пользователь " + userChatId + " просрочил дату" +
                    " отчёта свяжитесь с ним, вот его контактные данные " + contactDataService.getContactsByChatId(userChatId)));
        } catch (Exception e) {
            // Если не удалось отправить в админ-чат — логируем ошибку
            System.err.println("Не удалось отправить уведомление волонтёрам: " + e.getMessage());
        }
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
