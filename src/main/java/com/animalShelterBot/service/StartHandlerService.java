package com.animalShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class StartHandlerService {

    @Resource
    private TelegramBot telegramBot;

    /**
     * Обработчик команды /start
     * @param chatId ID чата
     */
    public void handleStart(long chatId) {
        SendMessage sendMessage = new SendMessage(chatId,
                """
                        Здравствуйте!\\n\\n\\
                        Я - ваш виртуальный помощник в поиске питомца.\\n\\n\\
                        Я расскажу вам о выбранном приюте, 
                        помогу подготовиться ко встрече с новым членом семьи, 
                        объясню, какие нужны документы, 
                        дам рекомендации по содержанию животного и отвечу на любые ваши вопросы.\\n\\n\\
                        А когда вы заберете домой своего питомца, 
                        я буду ждать ежедневных отчетов о его самочувствии в течение испытательного срока.\\n\\n\\
                        Ваш новый друг ждёт вас!
                        """);
        sendMessage.parseMode(ParseMode.Markdown);
        telegramBot.execute(sendMessage);
    }
}
