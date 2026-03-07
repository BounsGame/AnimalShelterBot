package com.animalShelterBot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AnimalShelterBotApplication {

    @Value("${telegram.bot.token}")
    private String botToken;

    public static void main(String[] args) {
        SpringApplication.run(AnimalShelterBotApplication.class, args);
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}