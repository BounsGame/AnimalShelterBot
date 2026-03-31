package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AdoptInfoService {

    @Resource
    private TelegramBot telegramBot;

    @Resource
    private UserSessionService userSessionService; // уже используется в других сервисах — добавляем так же

    /**
     * Как взять животное
     */
    public void handleAdoptInfo(long chatId) {
        String text = "🐾 *Как взять животное:*\n" + "1️⃣ Заполните анкету потенциального хозяина\n" + "2️⃣ Дождитесь звонка волонтёра\n" + "3️⃣ Приезжайте знакомиться с питомцем\n" + "4️⃣ Подпишите договор и заберите друга!";

        sendMessageWithMarkdown(chatId, text);

        sendAdoptDetailsMenu(chatId);
    }

    /**
     * Отправляет меню второго этапа — выбор рекомендаций
     */
    private void sendAdoptDetailsMenu(long chatId) {
        AnimalType shelterType = userSessionService.getShelterType(chatId);
        String animalEmoji = shelterType == AnimalType.CAT ? "🐱" : "🐕";

        String text = animalEmoji + " *Дополнительные рекомендации* — выберите интересующий раздел:";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(new InlineKeyboardButton("📋 Документы").callbackData("ADOPT_DOCS"));
        keyboard.addRow(new InlineKeyboardButton("🚗 Транспортировка").callbackData("ADOPT_TRANSPORT"));
        keyboard.addRow(new InlineKeyboardButton("🏠 Обустройство дома (щенок/котёнок)").callbackData("ADOPT_HOME_PUPPY"));
        keyboard.addRow(new InlineKeyboardButton("🏠 Обустройство дома (взрослый)").callbackData("ADOPT_HOME_ADULT"));
        keyboard.addRow(new InlineKeyboardButton("🏠 Обустройство дома (с ОВЗ)").callbackData("ADOPT_HOME_SPECIAL"));
        keyboard.addRow(new InlineKeyboardButton("🤝 Знакомство до выдачи питомца").callbackData("ADOPT_MEET_TIPS"));

        // Только для собак — кинологические советы
        if (shelterType == AnimalType.DOG) {
            keyboard.addRow(new InlineKeyboardButton("🎓 Советы кинолога").callbackData("ADOPT_K9_TIPS"));
            keyboard.addRow(new InlineKeyboardButton("📞 Проверенные кинологи").callbackData("ADOPT_K9_CONTACTS"));
            keyboard.addRow(new InlineKeyboardButton("🚫 Причины отказа").callbackData("ADOPT_REJECTION_REASONS"));
        }

        SendMessage message = new SendMessage(chatId, text);
        message.parseMode(ParseMode.Markdown);
        message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    /**
     * Обрабатывает нажатие на кнопки второго этапа
     */
    public void handleAdoptCallback(String callbackData, long chatId) {
        String text = "";

        switch (callbackData) {
            case "ADOPT_DOCS":
                text = getDocumentsText();
                break;
            case "ADOPT_TRANSPORT":
                text = getTransportTips();
                break;
            case "ADOPT_MEET_TIPS":
                text = getMeetTips();
                break;
            case "ADOPT_HOME_PUPPY":
                text = getHomeSetupPuppy();
                break;
            case "ADOPT_HOME_ADULT":
                text = getHomeSetupAdult();
                break;
            case "ADOPT_HOME_SPECIAL":
                text = getHomeSetupSpecial();
                break;
            case "ADOPT_K9_TIPS": // только для собак
                text = getK9Tips();
                break;
            case "ADOPT_K9_CONTACTS": // только для собак
                text = getK9Contacts();
                break;
            case "ADOPT_REJECTION_REASONS": // причины отказа
                text = getRejectionReasons();
                break;
            default:
                return; // неизвестная команда — игнорируем
        }

        if (!text.isEmpty()) {
            sendMessageWithMarkdown(chatId, text);
        }
    }

    private String getDocumentsText() {
        return "📋 *Документы для выдачи питомца:*\n\n" + "• Паспорт РФ\n" + "• Заявление об обязательстве содержания животного\n" + "• Справка с работы/учёбы (подтверждение занятости)\n" + "• Фото жилища (показ условий проживания)\n" + "• Письменное согласие всех членов семьи";
    }

    private String getTransportTips() {
        return "🚗 *Рекомендации по транспортировке:*\n\n" + "• Используйте переноску (для котов) или намордник и поводок (для собак)\n" + "• Убедитесь, что животное не испытывает стресса\n" + "• Не кормите за 3–4 часа до поездки\n" + "• Возьмите с собой воду и одеяло с привычным запахом";
    }

    private String getMeetTips() {
        return "🤝 *Как правильно познакомиться с животным:*\n\n" + "• Подходите спокойно, без резких движений\n" + "• Дайте питомцу самому решить — подойдёт ли он к вам\n" + "• Не трогайте еду, миски и игрушки\n" + "• Если есть дети — пусть наблюдают со стороны\n" + "• Не форсируйте контакт — первое знакомство может занять время";
    }

    private String getHomeSetupPuppy() {
        return "🏠 *Обустройство дома для щенка/котёнка:*\n\n" + "• Выделите отдельное место для сна (лежанка, домик)\n" + "• Уберите опасные предметы\n" + "• Купите миски, игрушки, когтеточку (для кошек)\n" + "• Приготовьтесь к приучению к лотку/поводку\n" + "• Обеспечьте тепло и чувство безопасности";
    }

    private String getHomeSetupAdult() {
        return "🏠 *Обустройство дома для взрослого животного:*\n\n" + "• Создайте спокойную зону без шума и сквозняков\n" + "• Не торопите адаптацию — первые дни могут быть сложными\n" + "• Сохраняйте привычный режим питания и прогулок\n" + "• Избегайте переизбытка внимания в первые дни\n" + "• Будьте терпеливы — старые привычки требуют времени";
    }

    private String getHomeSetupSpecial() {
        return "🏠 *Обустройство дома для животного с ОВЗ:*\n\n" + "• Уберите пороги и барьеры (для слабовидящих или с проблемами передвижения)\n" + "• Используйте звуковые маркеры (колокольчики на дверях)\n" + "• Обустройте лежанку на уровне пола, без подъёмов\n" + "• Для слепых — сохраняйте постоянную расстановку мебели\n" + "• Для парализованных — регулярный уход и смена положения тела";
    }

    // Только для собак
    private String getK9Tips() {
        return "🎓 *Советы кинолога по первичному общению с собакой:*\n\n" + "• Не смотрите прямо в глаза — это может восприниматься как вызов\n" + "• Подходите сбоку, не сверху\n" + "• Предложите руку для обнюхивания, не пытайтесь сразу гладить\n" + "• Говорите спокойно, без резких интонаций\n" + "• Не делайте резких движений и не протягивайте игрушки сразу";
    }

    // Только для собак
    private String getK9Contacts() {
        return "📞 *Проверенные кинологи (Москва):*\n\n" + "• *Иван Петров* — специалист по реабилитации агрессивных собак\n" + "  📱 +7 (916) 123-45-67\n" + "  📍 Москва, ЮЗАО\n\n" + "• *Анна Сидорова* — тренировка щенков и командное послушание\n" + "  📱 +7 (926) 987-65-43\n" + "  📍 Москва, ЦАО\n\n" + "• *Школа дрессировки «Пёс и человек»*\n" + "  🌐 https://dogschool.ru\n" + "  📍 Метро «Калужская»";
    }

    // Причины отказа в усыновлении (актуально для собак)
    private String getRejectionReasons() {
        return "🚫 *Причины, по которым могут отказать в выдаче собаки:*\n\n" + "• Несоответствие жилищных условий (маленькая квартира, нет двора)\n" + "• Отсутствие опыта содержания собак крупных пород\n" + "• Частые командировки или длительные отъезды\n" + "• Наличие маленьких детей без наблюдения взрослых\n" + "• Желание использовать собаку как охранника\n" + "• Отказ от подписания договора ответственного содержания\n" + "• Негативное отношение соседей или членов семьи";
    }

    private void sendMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
    }

    protected void sendMessageWithMarkdown(long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        message.parseMode(ParseMode.Markdown);
        telegramBot.execute(message);
    }
}