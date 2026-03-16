package com.animalShelterBot.service;

import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.model.State;
import com.animalShelterBot.model.UserSession;
import com.animalShelterBot.repository.UserSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления сессией пользователя в Telegram-боте.
 * <p>
 * Обеспечивает сохранение и получение состояния диалога ({@link State}),
 * типа выбранного приюта ({@link AnimalType}) и других данных сессии через JPA-репозиторий.
 * </p>
 *
 * <h2>Проблема, решённая в этом классе:</h2>
 * Ранее методы использовали {@code userSessionRepository.getReferenceById(chatId)},
 * который возвращает прокси-объект (lazy loading). При попытке вызвать геттер или сеттер
 * вне активной Hibernate-сессии возникала ошибка:
 * <pre>
 * org.hibernate.LazyInitializationException: Could not initialize proxy - no session
 * </pre>
 *
 * <h2>Решение:</h2>
 * <ul>
 *   <li>Добавлена аннотация {@link Transactional} ко всем методам, изменяющим сущность,
 *       чтобы гарантировать наличие активной сессии при работе с прокси.</li>
 *   <li>Заменён {@code getReferenceById()} на {@code findById()} в методах чтения,
 *       чтобы избежать ленивой инициализации там, где она не нужна.</li>
 *   <li>Добавлена проверка существования сессии перед созданием новой.</li>
 *   <li>Улучшена обработка случаев, когда сессия отсутствует — возвращается значение по умолчанию.</li>
 * </ul>
 *
 * @author Олег Мираков (Team: Animal Shelter)
 * @version 1.1 (исправлено: 2026-03-07)
 * @since 2026-03-07
 */
@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * Устанавливает состояние "ожидание выбора приюта".
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setStateWaitingForShelter(Long chatId) {
        UserSession session = findOrCreateSession(chatId);
        session.setState(State.WAITING_FOR_SHELTER);
        userSessionRepository.save(session);
    }

    /**
     * Устанавливает состояние "в главном меню".
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setStateInMainMenu(Long chatId) {
        System.out.println("in setMain");
        UserSession session = findOrCreateSession(chatId);
        session.setState(State.IN_MAIN_MENU);
        userSessionRepository.save(session);
    }

    /**
     * Устанавливает состояние "ожидание отчёта".
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setStateAwaitingReport(Long chatId) {
        UserSession session = findOrCreateSession(chatId);
        session.setState(State.AWAITING_REPORT);
        userSessionRepository.save(session);
    }

    /**
     * Устанавливает состояние "вызван волонтёр".
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setStateVolunteerCalled(Long chatId) {
        UserSession session = findOrCreateSession(chatId);
        session.setState(State.VOLUNTEER_CALLED);
        userSessionRepository.save(session);
    }

    /**
     * Устанавливает тип приюта — кошки.
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setShelterTypeCat(Long chatId) {
        UserSession session = findOrCreateSession(chatId);
        session.setShelterType(AnimalType.CAT);
        userSessionRepository.save(session);
    }

    /**
     * Устанавливает тип приюта — собаки.
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void setShelterTypeDog(Long chatId) {
        UserSession session = findOrCreateSession(chatId);
        session.setShelterType(AnimalType.DOG);
        userSessionRepository.save(session);
    }

    /**
     * Получает текущее состояние пользователя.
     * Если сессия не найдена — возвращает состояние по умолчанию.
     *
     * @param chatId идентификатор чата пользователя
     * @return текущее состояние диалога
     */
    public State getState(Long chatId) {
        return userSessionRepository.findById(chatId).map(UserSession::getState).orElse(State.WAITING_FOR_SHELTER);
    }

    /**
     * Получает выбранный тип приюта (кошки/собаки).
     *
     * @param chatId идентификатор чата пользователя
     * @return тип приюта или null, если не выбран
     */
    public AnimalType getShelterType(Long chatId) {
        return userSessionRepository.findById(chatId).map(UserSession::getShelterType).orElse(null);
    }

    /**
     * Возвращает полную сессию пользователя.
     *
     * @param chatId идентификатор чата
     * @return объект сессии или null, если не найден
     */
    public UserSession getUserSession(Long chatId) {
        return userSessionRepository.findById(chatId).orElse(null);
    }

    /**
     * Создаёт новую сессию, если она ещё не существует.
     *
     * @param chatId идентификатор чата пользователя
     */
    @Transactional
    public void addNewUserSession(Long chatId) {
        findOrCreateSession(chatId);
    }

    // Внутренний метод: находит сессию или создаёт новую
    public UserSession findOrCreateSession(Long chatId) {
        return userSessionRepository.findById(chatId).orElseGet(() -> {
            System.out.println("in create");
            UserSession newSession = new UserSession(chatId);
            newSession.setState(State.WAITING_FOR_SHELTER);
            return userSessionRepository.save(newSession);
        });
    }
}