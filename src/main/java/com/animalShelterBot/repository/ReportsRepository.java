package com.animalShelterBot.repository;

import com.animalShelterBot.model.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Класс репозитория для таблицы отчётов
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Repository
public interface ReportsRepository extends JpaRepository<Reports,Long> {

    public List<Reports> findByChatId(Long chatId);
}
