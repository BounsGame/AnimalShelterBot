package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ShelterInfoService {

    @Resource
    private TelegramBot telegramBot;

    private final UserSessionService userSessionService;

    ShelterInfoService(UserSessionService userSessionService){
        this.userSessionService = userSessionService;
    }

    /** Проверка типа приюта из БД */
    public void handleShelterInfo(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            // Пример:
            sendMessage(chatId, "🐱 Информация о кошачьем приюте:\n• Адрес...\n• Режим работы...");
        }
        else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            // Пример:
            sendMessage(chatId, "🐕 Информация о собачьем приюте:\n• Адрес...\n• Режим работы...");
        }
        else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
