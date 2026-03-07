package com.animalShelterBot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Сессия пользователя — хранит состояние диалога.
 * <p>
 * Используется для управления FSM (конечным автоматом) бота.
 * </p>
 *
 * @author Матвеев Данил (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */
@Entity
@Table(name = "user_sessions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSession {

    // Используем chatId как ID — он уникален для каждого пользователя в Telegram
    @Id
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state = State.WAITING_FOR_SHELTER;

    @Column(name = "shelter_type", length = 10)
    private AnimalType shelterType; // CAT / DOG

    // Необходимый пустой конструктор для JPA
    public UserSession() {
    }

    // Конструктор для удобного создания
    public UserSession(Long chatId) {
        this.chatId = chatId;
    }

    // Геттеры и сеттеры
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public AnimalType getShelterType() {
        return shelterType;
    }

    public void setShelterType(AnimalType shelterType) {
        this.shelterType = shelterType;
    }
}