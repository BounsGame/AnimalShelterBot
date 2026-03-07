package com.animalShelterBot.model;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

/**
 * Класс для таблицы хранящей контактную информацию
 * хранит в себе информацию о пользователе, а также переданную им информацию для связи
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Entity
@Table(name = "contacts")
public class ContactData {

    @Id
    @Column
    private Long chatId;

    @Column
    private String contacts;

    public ContactData(Long chatId, String contacts) {
        try {
            if (chatId != null) {
                this.chatId = chatId;
                this.contacts = contacts;
            } else throw new IllegalArgumentException("id чата должно быть не null");
        }catch (IllegalArgumentException e){
            System.out.println("произошла ошибка id чата должно быть не null");
        }
    }

    public Long getChatId() {
        return chatId;
    }

    public String getContacts() {
        return contacts;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString(){
        return "пользователь " + chatId + " оставил эти данный для контакта: " + contacts;
    }
}
