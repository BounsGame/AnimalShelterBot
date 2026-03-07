package com.animalShelterBot.service;

import com.animalShelterBot.model.Reports;
import com.animalShelterBot.repository.ReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Класс сервиса отчётов для взаимодействия с БД
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Service
public class ReportService {

    @Autowired
    private ReportsRepository reportsRepository;

    public void addNewReport(Long chatId,String diet, String healthAndAdaptation,String behaviorChanges, String photoFileId){
        reportsRepository.save(new Reports(chatId,diet,healthAndAdaptation,behaviorChanges,photoFileId, LocalDateTime.now()));
    }

    public void getReportByChatId(Long chatId){
        reportsRepository.findByChatId(chatId);
    }
}
