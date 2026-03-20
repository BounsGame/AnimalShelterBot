package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import jakarta.annotation.Resource;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.io.IOException;

@Service
public class ShelterInfoService {

    @Resource
    private TelegramBot telegramBot;

    private final UserSessionService userSessionService;
    private static final Logger logger = LoggerFactory.getLogger(ShelterInfoService.class);

    ShelterInfoService(TelegramBot telegramBot, UserSessionService userSessionService) {
        this.telegramBot = telegramBot;
        this.userSessionService = userSessionService;
    }

    /**
     * Отправляет меню для перехода к интересующей информации о приюте
     */
    public void handleShelterInfo(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createButton("Режим работы", "SHELTER_SCHEDULE"),
                createButton("Адрес", "SHELTER_ADDRESS"));
        keyboard.addRow(createButton("Контакты охраны", "SECURITY_CONTACTS"),
                createButton("Рекомендации", "SAFETY_RULES"));
        SendMessage message = new SendMessage(chatId, "Выберите, что вас интересует:\n• Режим работы приюта.\n• Адрес приюта и схема проезда.\n• Контактные данные охраны для оформления пропуска.\n• Рекомендации по технике безопасности на территории приюта");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    /**
     * Вспомогательный метод создания кнопки для сокращения кода и удобства поддержки
     */
    private InlineKeyboardButton createButton(String buttonName, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(buttonName);
        button.callbackData(callbackData);
        return button;
    }

    /**
     * Обработка нажатий на кнопки меню информации о приюте
     */
    public void handleShelterInfoMenu(long chatId, String callbackData) {
        switch (callbackData) {
            case "SHELTER_SCHEDULE":
                sendShelterSchedule(chatId);
                break;
            case "SHELTER_ADDRESS":
                sendShelterAddress(chatId);
                break;
            case "SECURITY_CONTACTS":
                sendSecurityContacts(chatId);
                break;
            case "SAFETY_RULES":
                sendSafetyRules(chatId);
                break;
            default:
                sendMessage(chatId, "Выберите кнопку");
        }
    }

    /**
     * Отправляет общие рекомендации о технике безопасности на территории приюта
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    private void sendSafetyRules(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            sendMessage(chatId, "🐱 Правила безопасности кошачьего приюта:\n• Вход только в сменной обуви или бахилах.\n• Не кормите кошек без разрешения сотрудников.\n• Не берите кошек на руки без разрешения сотрудников.\n• Не оставляйте двери и окна открытыми. \n• Не приводите других животных.\n• Не фотографируйте животных без разрешения сотрудников.\n• Мойте руки до и после контакта с животными.");
        } else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            sendMessage(chatId, "🐕 Правила безопасности собачьего приюта:\n• Вход только в закрытой одежде и удобной обуви.\n• Не кормите собак без разрешения сотрудников.\n• Не подходите к незнакомым собакам без сотрудника.\n• Не оставляйте детей без присмотра на территории приюта.\n• Не делайте резких движений и не издавайте громких звуков.\n• Не фотографируйте животных без разрешения сотрудников.\n• Не оставляйте двери открытыми.\n• Мойте руки до и после контакта с животными.");
        } else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    /**
     * Отправляет контактные данные охраны для оформления пропуска
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    private void sendSecurityContacts(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            sendMessage(chatId, "🐱 Контактные данные охраны кошачьего приюта:\n• Тел. 8 905 123 45 67\n• Эл.почта: cats@shelters.ru");
        } else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            sendMessage(chatId, "🐕 Контактные данные охраны собачьего приюта:\n• Тел. 8 910 123 45 67\n• Эл.почта: dogs@shelters.ru");
        } else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    /**
     * Отправляет адрес приюта и схему проезда
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    private void sendShelterAddress(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            sendMessage(chatId, "🐱 Адрес кошачьего приюта:\n• ул. Кошкина, дом 12");
            sendImageFromResources(chatId, "images/cat_shelter_map.jpg");
        } else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            sendMessage(chatId, "🐕 Адрес собачьего приюта:\n• ул. Сонная, дом 20");
            sendImageFromResources(chatId, "images/dog_shelter_map.jpg");
        } else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    /**
     * Отправляет расписание работы приюта
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    private void sendShelterSchedule(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            sendMessage(chatId, "🐱 Режим работы кошачьего приюта:\n• С понедельника по пятницу: 10:00—18:00\n• Суббота и воскресенье: 11:00—16:00");
        } else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            sendMessage(chatId, "🐕 Режим работы собачьего приюта:\n• С понедельника по пятницу: 09:00—20:00\n• Суббота и воскресенье: 10:00—18:00");
        } else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }

    /**
     * Вспомогательный метод отправки изображения для чистоты кода и удобства поддержки
     * @param chatId идентификатор чата, куда отправляется изображение
     * @param resourcePath путь к файлу изображения
     */
    private void sendImageFromResources(long chatId, String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                sendMessage(chatId, "Схема проезда не найдена. Обратитесь к администратору");
                return;
            }
            SendPhoto sendPhoto = new SendPhoto(chatId, resource.getFile());
            telegramBot.execute(sendPhoto);
        } catch (IOException e) {
            logger.error("Ошибка при отправке изображения из ресурсов: {}", resourcePath, e);
            sendMessage(chatId, "Ошибка при загрузке изображения");
        }
    }
}
