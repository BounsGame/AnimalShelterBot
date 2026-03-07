package com.animalShelterBot.repository;


import com.animalShelterBot.model.ContactData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Класс репозитория для таблицы контактных данных
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Repository
public interface ContactsRepository extends JpaRepository<ContactData, Long> {

}
