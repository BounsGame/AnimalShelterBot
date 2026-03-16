import com.animalShelterBot.AnimalShelterBotApplication;
import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.model.State;
import com.animalShelterBot.model.UserSession;
import com.animalShelterBot.repository.UserSessionRepository;
import com.animalShelterBot.service.UserSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private UserSessionService userSessionService;


    @Test
    public void findOrCreateUserSessionFindTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.IN_MAIN_MENU);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));

        UserSession result = userSessionService.findOrCreateSession(chatId);

        verify(userSessionRepository).findById(any());
        verify(userSessionRepository, never()).save(any());
        assertNotNull(result);
        assertEquals(existUserSession, result);
    }

    @Test
    public void findOrCreateUserSessionCreateTest() {
        Long chatId = 223L;

        when(userSessionRepository.findById(any())).thenReturn(Optional.empty());
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = userSessionService.findOrCreateSession(chatId);

        verify(userSessionRepository).save(any());
        assertNotNull(result);
    }
    //раздели тесты
    @Test
    public void setStateMainTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setStateInMainMenu(chatId);

        assertEquals(State.IN_MAIN_MENU, existUserSession.getState());
    }

    @Test
    public void setStateVolunteerTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setStateVolunteerCalled(chatId);

        assertEquals(State.VOLUNTEER_CALLED, existUserSession.getState());
    }

    @Test
    public void setStateShelterTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setStateWaitingForShelter(chatId);

        assertEquals(State.WAITING_FOR_SHELTER, existUserSession.getState());
    }

    @Test
    public void setStateReportTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setStateAwaitingReport(chatId);

        assertEquals(State.AWAITING_REPORT, existUserSession.getState());
    }

    @Test
    public void setCatTypeTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setShelterTypeCat(chatId);

        assertEquals(AnimalType.CAT, existUserSession.getShelterType());
    }

    @Test
    public void setDogTypeTest() {
        Long chatId = 123L;
        UserSession existUserSession = new UserSession(chatId);
        existUserSession.setState(State.WAITING_FOR_SHELTER);

        when(userSessionRepository.findById(any())).thenReturn(Optional.of(existUserSession));
        when(userSessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        userSessionService.setShelterTypeDog(chatId);

        assertEquals(AnimalType.DOG,existUserSession.getShelterType());
    }
}
