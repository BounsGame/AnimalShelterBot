package com.animalShelterBot;

import com.animalShelterBot.model.UserSession;
import com.animalShelterBot.service.StartHandlerService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Основной класс Telegram-бота — обрабатывает входящие обновления от пользователей.
 * <p>
 * Этот компонент отвечает за:
 * <ul>
 *   <li>Приём и обработку сообщений и нажатий на кнопки</li>
 *   <li>Запуск начального сценария при получении команды {@code /start}</li>
 *   <li>Отправку клавиатуры для выбора типа приюта (кошки/собаки)</li>
 *   <li>Сохранение выбора пользователя в БД через {@link UserSessionService}</li>
 * </ul>
 * </p>
 *
 * <h2>Принцип работы:</h2>
 * <ol>
 *   <li>Пользователь отправляет {@code /start} → бот приветствует его ({@link StartHandlerService})</li>
 *   <li>Бот отправляет инлайн-кнопки: "Приют для кошек" / "Приют для собак"</li>
 *   <li>При нажатии кнопки:
 *     <ul>
 *       <li>Фиксируется выбор в {@link UserSession} через соответствующий метод сервиса</li>
 *       <li>Отправляется подтверждающее сообщение</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h2>Внедряемые зависимости:</h2>
 * <ul>
 *   <li>{@link TelegramBot} — клиент для взаимодействия с Telegram Bot API</li>
 *   <li>{@link StartHandlerService} — сервис для обработки команды старта</li>
 *   <li>{@link UserSessionService} — сервис для сохранения состояния и выбора приюта</li>
 * </ul>
 *
 * <h2>Безопасность и потоки:</h2>
 * <p>
 * Обновления приходят асинхронно через OkHttp в фоновом потоке.
 * Все вызовы к базе данных происходят в рамках транзакций, обеспечивая потокобезопасность.
 * </p>
 *
 * @author Олег Мираков (Team: Animal Shelter)
 * @version 1.1
 * @since 2026-03-07
 */
@Component
public class AnimalShelterTelegramBot {

    private final TelegramBot telegramBot;
    private final StartHandlerService startHandlerService;
    private final UserSessionService userSessionService;

    /**
     * Токен бота, загружаемый из {@code application.properties}.
     * Не используется напрямую — передаётся в {@link TelegramBot}, созданный в конфигурации.
     */
    @Value("${telegram.bot.token}")
    private String botToken;

    /**
     * Конструктор для внедрения зависимостей.
     * Spring автоматически предоставляет экземпляры:
     * <ul>
     *   <li>{@link TelegramBot} — зарегистрирован как бин в {@link AnimalShelterBotApplication}</li>
     *   <li>{@link StartHandlerService} — содержит логику приветствия</li>
     *   <li>{@link UserSessionService} — управляет состоянием пользователя в БД</li>
     * </ul>
     *
     * @param telegramBot         клиент для отправки сообщений в Telegram
     * @param startHandlerService сервис для обработки команды /start
     * @param userSessionService  сервис для хранения состояния диалога
     */
    public AnimalShelterTelegramBot(TelegramBot telegramBot, StartHandlerService startHandlerService, UserSessionService userSessionService) {
        this.telegramBot = telegramBot;
        this.startHandlerService = startHandlerService;
        this.userSessionService = userSessionService;
    }

    /**
     * Инициализирует прослушивание обновлений от Telegram.
     * Устанавливает слушатель, который будет вызывать {@link #processUpdate(Update)} для каждого обновления.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * Основной обработчик входящих обновлений.
     * Разделяет типы обновлений:
     * <ul>
     *   <li>Сообщение с текстом — например, команда {@code /start}</li>
     *   <li>Нажатие на inline-кнопку — выбор приюта</li>
     * </ul>
     *
     * @param update объект обновления от Telegram
     */
    private void processUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            long chatId = update.message().chat().id();
            String messageText = update.message().text();

            if ("/start".equals(messageText)) {
                startHandlerService.handleStart(chatId);
                sendShelterChoice(chatId);
            }
        } else if (update.callbackQuery() != null) {
            String data = update.callbackQuery().data();
            long chatId = update.callbackQuery().message().chat().id();

            if ("SHELTER_CAT".equals(data)) {
                userSessionService.setShelterTypeCat(chatId);
                sendMessage(chatId, "Вы выбрали приют для кошек. Добро пожаловать!");
            } else if ("SHELTER_DOG".equals(data)) {
                userSessionService.setShelterTypeDog(chatId);
                sendMessage(chatId, "Вы выбрали приют для собак. Добро пожаловать!");
            }
        }
    }

    /**
     * Отправляет пользователю инлайн-клавиатуру для выбора приюта.
     *
     * @param chatId идентификатор чата, куда отправить сообщение
     */
    private void sendShelterChoice(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(new InlineKeyboardButton("Приют для кошек").callbackData("SHELTER_CAT"), new InlineKeyboardButton("Приют для собак").callbackData("SHELTER_DOG"));

        SendMessage sendMessage = new SendMessage(chatId, "Выберите приют:");
        sendMessage.replyMarkup(keyboard);

        telegramBot.execute(sendMessage);
    }

    /**
     * Универсальный метод для отправки текстового сообщения.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     */
    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }
}