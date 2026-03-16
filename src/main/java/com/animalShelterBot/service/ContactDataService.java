package com.animalShelterBot.service;

import com.animalShelterBot.model.ContactData;
import com.animalShelterBot.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Класс сервиса контактных данных для взаимодействия с БД
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Service
public class ContactDataService {

    @Autowired
    private ContactsRepository contactsRepository;

    public void addNewContacts(Long chatId, String contacts){
        contactsRepository.save(new ContactData(chatId,contacts));
    }

    //при использовании метода делайте try на EntityNotFoundException
    public ContactData getContactsByChatId(Long chatId){
        return contactsRepository.getReferenceById(chatId);
    }
}
