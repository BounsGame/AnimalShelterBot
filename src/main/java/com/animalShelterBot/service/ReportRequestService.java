package com.animalShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ReportRequestService {

    private final UserSessionService userSessionService;

    @Resource
    private TelegramBot telegramBot;

    ReportRequestService (UserSessionService userSessionService){
        this.userSessionService = userSessionService;
    }
    /** Запрос отчёта о питомце */
    public void handleReportRequest(long chatId) {
        // Переключаем состояние на ожидание отчёта
        userSessionService.setStateAwaitingReport(chatId);
        sendMessage(chatId, "📝 Отправьте фото и короткий текст о том, как дела у вашего питомца.");
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
