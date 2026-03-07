package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.model.State;
import com.animalShelterBot.model.UserSession;
import com.animalShelterBot.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Класс сервиса сессии пользователя для взаимодействия с БД
 *
 * @author Matveev Danil (Team: Animal Shelter)
 * @version 1.0
 * @since 2026-03-07
 */

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    public void setStateWaitingForShelter(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setState(State.WAITING_FOR_SHELTER);
        userSessionRepository.save(userSession);
    }

    public void setStateInMainMenu(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setState(State.IN_MAIN_MENU);
        userSessionRepository.save(userSession);
    }

    public void setStateAwaitingReport(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setState(State.AWAITING_REPORT);
        userSessionRepository.save(userSession);
    }

    public void setStateVolunteerCalled(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setState(State.VOLUNTEER_CALLED);
        userSessionRepository.save(userSession);
    }

    public void setShelterTypeCat(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setShelterType(AnimalType.CAT);
        userSessionRepository.save(userSession);
    }

    public void setShelterTypeDog(Long chatId){
        UserSession userSession = userSessionRepository.getReferenceById(chatId);
        userSession.setShelterType(AnimalType.DOG);
        userSessionRepository.save(userSession);
    }

    public State getState(Long chatId){
        return userSessionRepository.getReferenceById(chatId).getState();
    }

    public AnimalType getShelterType(Long chatId){
        return userSessionRepository.getReferenceById(chatId).getShelterType();
    }

    public UserSession getUserSession(Long chatId){
        return userSessionRepository.getReferenceById(chatId);
    }

    public void addNewUserSession(Long chatId){
        userSessionRepository.save(new UserSession(chatId));
    }
}
