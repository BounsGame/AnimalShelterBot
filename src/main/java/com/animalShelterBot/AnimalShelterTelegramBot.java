package com.animalShelterBot;

import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.model.State;
import com.animalShelterBot.model.UserSession;
import com.animalShelterBot.service.StartHandlerService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Основной класс Telegram-бота — обрабатывает входящие обновления от пользователей.
 *
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

    // ID чата волонтёров (вынеси в application.properties)
    @Value("${telegram.bot.volunteer-chat-id:123456789}")
    private String volunteerChatId;

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
        // === Обработка текстовых сообщений ===
        if (update.message() != null && update.message().text() != null) {
            long chatId = update.message().chat().id();
            String messageText = update.message().text();

            if ("/start".equals(messageText)) {
                startHandlerService.handleStart(chatId);
                sendShelterChoice(chatId);
            }
            // Здесь потом можно добавить обработку текстовых сообщений в других состояниях
        }

        // === Обработка нажатий на inline-кнопки ===
        else if (update.callbackQuery() != null) {
            String data = update.callbackQuery().data();
            long chatId = update.callbackQuery().message().chat().id();

            // 🔹 Существующая логика: выбор приюта
            if ("SHELTER_CAT".equals(data)) {
                userSessionService.setShelterTypeCat(chatId);
                userSessionService.setStateInMainMenu(chatId); // ✅ Переход в главное меню
                sendMessage(chatId, "🐱 Вы выбрали приют для кошек. Добро пожаловать!");
                sendMainMenu(chatId); // ✅ Показываем главное меню
            }
            else if ("SHELTER_DOG".equals(data)) {
                userSessionService.setShelterTypeDog(chatId);
                userSessionService.setStateInMainMenu(chatId); // ✅ Переход в главное меню
                sendMessage(chatId, "🐕 Вы выбрали приют для собак. Добро пожаловать!");
                sendMainMenu(chatId); // ✅ Показываем главное меню
            }

            // 🔹 НОВАЯ логика: главное меню
            else if ("MENU_INFO".equals(data)) {
                handleShelterInfo(chatId);
            }
            else if ("MENU_ADOPT".equals(data)) {
                handleAdoptInfo(chatId);
            }
            else if ("MENU_REPORT".equals(data)) {
                handleReportRequest(chatId);
            }
            else if ("MENU_VOLUNTEER".equals(data)) {
                handleVolunteerCall(chatId);
            }

            // ✅ Обязательно "убираем" нажатие с кнопки
            answerCallbackQuery(update.callbackQuery().id(), "Обработка...");
        }
    }

    // === Методы отправки клавиатур ===

    /  /**
     * Отправляет пользователю инлайн-клавиатуру для выбора приюта.
     *
     * @param chatId идентификатор чата, куда отправить сообщение
     */

    private void sendShelterChoice(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Приют для кошек").callbackData("SHELTER_CAT"),
                new InlineKeyboardButton("Приют для собак").callbackData("SHELTER_DOG")
        );
        SendMessage message = new SendMessage(chatId, "Выберите приют:");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    /** Отправляет главное меню с 4 кнопками */
    private void sendMainMenu(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("🏠 О приюте").callbackData("MENU_INFO"),
                new InlineKeyboardButton("🐾 Как взять животное").callbackData("MENU_ADOPT"),
                new InlineKeyboardButton("📝 Отчёт о питомце").callbackData("MENU_REPORT"),
                new InlineKeyboardButton("🆘 Позвать волонтёра").callbackData("MENU_VOLUNTEER")
        );
        SendMessage message = new SendMessage(chatId, "Выберите действие:");
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    // === Обработчики кнопок главного меню ===

    /** 🔹 Проверка типа приюта из БД + заготовки if/else */
    private void handleShelterInfo(long chatId) {
        // Получаем тип приюта из БД через сервис
        AnimalType shelterType = userSessionService.getShelterType(chatId);

        if (shelterType == AnimalType.CAT) {
            // 🐱 Ветка для кошачьего приюта
            // Заполнишь позже: текст, картинки, ссылки и т.д.
            // Пример:
            // sendMessage(chatId, "🐱 Информация о кошачьем приюте:\n• Адрес...\n• Режим работы...");
        }
        else if (shelterType == AnimalType.DOG) {
            // 🐕 Ветка для собачьего приюта
            // Заполнишь позже
            // Пример:
            // sendMessage(chatId, "🐕 Информация о собачьем приюте:\n• Адрес...\n• Режим работы...");
        }
        else {
            // Если тип приюта не определён (защита от ошибок)
            sendMessage(chatId, "⚠️ Сначала выберите тип приюта через /start");
        }
    }

    /** Как взять животное */
    private void handleAdoptInfo(long chatId) {
        String text = "🐾 *Как взять животное:*\n" +
                "1️⃣ Заполните анкету потенциального хозяина\n" +
                "2️⃣ Дождитесь звонка волонтёра\n" +
                "3️⃣ Приезжайте знакомиться с питомцем\n" +
                "4️⃣ Подпишите договор и заберите друга!";
        sendMessageWithMarkdown(chatId, text);
    }

    /** Запрос отчёта о питомце */
    private void handleReportRequest(long chatId) {
        // Переключаем состояние на ожидание отчёта
        userSessionService.setStateAwaitingReport(chatId);
        sendMessage(chatId, "📝 Отправьте фото и короткий текст о том, как дела у вашего питомца.");
    }

    /** 🔹 Вызов волонтёра */
    private void handleVolunteerCall(long chatId) {
        // 1. Подтверждение пользователю
        sendMessage(chatId, "✅ Волонтёр уже уведомлён! Ожидайте ответа в течение 5-10 минут.");

        // 2. Обновляем состояние (опционально)
        userSessionService.setStateVolunteerCalled(chatId);

        // 3. Уведомление волонтёрам (админ-чат)
        String alert = String.format("🆘 *Вызов волонтёра!*\n" +
                        "👤 Пользователь: `%d`\n" +
                        "⏰ Время: `%s`",
                chatId,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
        );
        SendMessage alertMessage = new SendMessage(volunteerChatId, alert);
        alertMessage.parseMode("Markdown");

        try {
            telegramBot.execute(alertMessage);
        } catch (Exception e) {
            // Если не удалось отправить в админ-чат — логируем ошибку
            System.err.println("Не удалось отправить уведомление волонтёрам: " + e.getMessage());
        }
    }

    // === Вспомогательные методы ===

    /**
     * Универсальный метод для отправки текстового сообщения.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     */

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }

    /** Отправка сообщения с Markdown-разметкой */
    private void sendMessageWithMarkdown(long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        message.parseMode("Markdown");
        telegramBot.execute(message);
    }

    /** Ответ на callbackQuery (чтобы убрать "часики" с кнопки) */
    private void answerCallbackQuery(String callbackQueryId, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQueryId);
        answer.text(text);
        answer.showAlert(false);
        telegramBot.execute(answer);
    }
}