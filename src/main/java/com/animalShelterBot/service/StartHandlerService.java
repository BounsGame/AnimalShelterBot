package com.animalShelterBot.service;

import com.animalShelterBot.model.UserSession;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class StartHandlerService {

    private final TelegramBot telegramBot;

    private final UserSessionService userSessionService;

    StartHandlerService (UserSessionService userSessionService, TelegramBot telegramBot){
        this.userSessionService = userSessionService;
        this.telegramBot = telegramBot;
    }

    /**
     * Обработчик команды /start
     * @param chatId ID чата
     */
    public void handleStart(long chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                """
                        Здравствуйте!
                        
                        Я - ваш виртуальный помощник в поиске питомца.
                        
                        Я расскажу вам о выбранном приюте, помогу подготовиться ко встрече с новым членом семьи, объясню, какие нужны документы, дам рекомендации по содержанию животного и отвечу на любые ваши вопросы.
                        
                        А когда вы заберете домой своего питомца, я буду ждать ежедневных отчетов о его самочувствии в течение испытательного срока.
                        
                        Ваш новый друг ждёт вас!
                        """);
        sendMessage.parseMode(ParseMode.Markdown);
        telegramBot.execute(sendMessage);
        sendShelterChoice(chatId);
    }

    /**
     * Отправляет пользователю инлайн-клавиатуру для выбора приюта.
     *
     * @param chatId идентификатор чата, куда отправить сообщение
     */

    public void sendShelterChoice(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для кошек").callbackData("CAT"),
                new InlineKeyboardButton("Приют для собак").callbackData("DOG")
        );
        SendMessage message = new SendMessage(chatId, "Выберите приют:");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    public void getShelterChoice (String data ,Long chatId){
        if ("CAT".equals(data)) {
            userSessionService.setShelterTypeCat(chatId);
            userSessionService.setStateInMainMenu(chatId); // ✅ Переход в главное меню
            sendMessage(chatId, "🐱 Вы выбрали приют для кошек. Добро пожаловать!");
            sendMainMenu(chatId); // ✅ Показываем главное меню
        }
        else if ("DOG".equals(data)) {
            userSessionService.setShelterTypeDog(chatId);
            userSessionService.setStateInMainMenu(chatId); // ✅ Переход в главное меню
            sendMessage(chatId, "🐕 Вы выбрали приют для собак. Добро пожаловать!");
            sendMainMenu(chatId); // ✅ Показываем главное меню
        }
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }

    /** Отправляет главное меню с 4 кнопками */
    public void sendMainMenu(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(new InlineKeyboardButton("🏠 О приюте").callbackData("MENU_INFO"),
                new InlineKeyboardButton("🐾 Как взять животное").callbackData("MENU_ADOPT"));
        keyboard.addRow(new InlineKeyboardButton("📝 Отчёт о питомце").callbackData("MENU_REPORT"),
                new InlineKeyboardButton("🆘 Позвать волонтёра").callbackData("MENU_VOLUNTEER"));
        SendMessage message = new SendMessage(chatId, "Выберите действие:");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }
}
