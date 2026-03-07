package com.animalShelterBot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Класс для таблицы хранящей отчёты владельца питомца
 * хранит в себе информацию о пользователе, фото питомца, рацион, общее самочувствие и изменения в поведении
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Entity
@Table(name = "reports")
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    //пользователь отправивший отчёт
    @Column
    private Long chatId;

    //текст о питании
    @Column
    private String diet;

    //текст для общего состояния питомца
    @Column
    private String healthAndAdaptation;

    //текст для привычек и изменения в поведении
    @Column
    private String behaviorChanges;

    //хранение фото
    // если не понятно почему фото так храниться: ТГ хранит фотки у себя можно по их id их заново подгружать таким образом
    @Column
    private String photoFileId;

    //время получения отчёта
    @Column
    private LocalDateTime dateTime;

    public Reports(Long chatId, String diet, String healthAndAdaptation, String behaviorChanges, String photoFileId, LocalDateTime dateTime) {
        try {
            if (chatId != null) {
                this.chatId = chatId;
                this.diet = diet;
                this.healthAndAdaptation = healthAndAdaptation;
                this.behaviorChanges = behaviorChanges;
                this.photoFileId = photoFileId;
                this.dateTime = dateTime;
            }else throw new IllegalArgumentException("chatId не должно быть null");
        }catch (IllegalArgumentException e){
            System.out.println("chatId не должно быть null");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getDiet() {
        return diet;
    }

    public String getHealthAndAdaptation() {
        return healthAndAdaptation;
    }

    public String getBehaviorChanges() {
        return behaviorChanges;
    }

    public String getPhotoFileId() {
        return photoFileId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setHealthAndAdaptation(String healthAndAdaptation) {
        this.healthAndAdaptation = healthAndAdaptation;
    }

    public void setBehaviorChanges(String behaviorChanges) {
        this.behaviorChanges = behaviorChanges;
    }

    public void setPhotoFileId(String photoFileId) {
        this.photoFileId = photoFileId;
    }

    @Override
    public String toString() {
        return "отчёт полученный " + dateTime + "/n" + "1)рацион питомца: " + diet + "/n" + "2)общее состояние питомца: "
                + healthAndAdaptation + "/n" + "3)изменение в поведении: " + behaviorChanges;
    }
}
