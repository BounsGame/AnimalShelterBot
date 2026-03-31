package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.model.ContactData;
import com.animalShelterBot.repository.ContactsRepository;
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
    private final ContactsRepository contactsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ShelterInfoService.class);

    ShelterInfoService(TelegramBot telegramBot, UserSessionService userSessionService, ContactsRepository contactsRepository) {
        this.telegramBot = telegramBot;
        this.userSessionService = userSessionService;
        this.contactsRepository = contactsRepository;
    }

    /**
     * Отправляет меню для перехода к интересующей информации о приюте
     */
    public void handleShelterInfo(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createButton("Описание", "SHELTER_DESCRIPTION"),
                createButton("Режим работы", "SHELTER_SCHEDULE"));
        keyboard.addRow(createButton("Адрес", "SHELTER_ADDRESS"),
                createButton("Контакты охраны", "SECURITY_CONTACTS"));
        keyboard.addRow(createButton("Рекомендации", "SAFETY_RULES"),
                createButton("Оставить контакты", "USER_CONTACTS"));
        keyboard.addRow(createButton("Вызов волонтера", "MENU_VOLUNTEER"));
        SendMessage message = new SendMessage(chatId, "Выберите, что вас интересует:\n• Описание приюта.\n• Режим работы приюта.\n• Адрес приюта и схема проезда.\n• Контактные данные охраны для оформления пропуска.\n• Рекомендации по технике безопасности на территории приюта.\n• Оставить контактные данные для связи с вами.\n• Позвать волонтера.");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
        userSessionService.setStateInShelterInfoMenu(chatId);
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
            case "SHELTER_DESCRIPTION":
                sendShelterDescription(chatId);
                break;
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
            case "USER_CONTACTS":
                userSessionService.setStateAwaitingContactInfo(chatId);
                sendMessage(chatId, "Пожалуйста, оставьте контактные данные для связи");
                break;
            default:
                sendMessage(chatId, "Выберите кнопку");
        }
    }

    /**
     * Принимает и сохраняет контактные данные для связи с пользователем
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    public void saveUserContacts(long chatId, String contactInfo) {
        if (contactInfo == null || contactInfo.trim().isEmpty()) {
            sendMessage(chatId, "Вы не указали контактные данные. Пожалуйста, попробуйте еще раз.");
            return;
        }
        ContactData contact = new ContactData(chatId, contactInfo.trim());
        contactsRepository.save(contact);
        sendMessage(chatId, "Ваши контактные данные успешно сохранены.");
        userSessionService.setStateInShelterInfoMenu(chatId);
    }

    /**
     * Отправляет описание приюта
     * @param chatId идентификатор чата, куда отправляется сообщение
     */
    private void sendShelterDescription(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            sendMessage(chatId, "🐱 Добро пожаловать в «Котодом»!\nЭто уютный приют для кошек, где каждый пушистый житель окружен заботой и вниманием. У нас животные получают ветеринарную помощь, полноценное питание и заботу. Приют открыт для посещений: можно познакомиться с обитателями и найти нового члена семьи.");
        } else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            sendMessage(chatId, "🐕 Добро пожаловать в приют «Верный друг»!\nЗдесь собаки обретают шанс на новую жизнь. У нас есть подопечные разных пород и возрастов. В приюте заботятся об их лечении и социализации. Мы активно ищем новые семьи для наших животных и помогаем им с адаптацией. Приходите, верный друг уже ждет вас.");
        } else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
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

    public void handleCallbackQuery(long chatId, String callbackData) {
        if (userSessionService.isInShelterInfoMenu(chatId)) {
            handleShelterInfoMenu(chatId, callbackData);
        }
    }
}
