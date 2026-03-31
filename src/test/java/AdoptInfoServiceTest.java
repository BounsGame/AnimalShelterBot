import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.service.AdoptInfoService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdoptInfoServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private AdoptInfoService adoptInfoService;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Test
    public void handleAdoptInfoSendsMainMessageTest() {
        long chatId = 123L;

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.DOG);

        adoptInfoService.handleAdoptInfo(chatId);

        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture());
        var allMessages = sendMessageCaptor.getAllValues();

        String firstText = allMessages.get(0).getParameters().get("text").toString();
        assert firstText.contains("Как взять животное:");
    }

    @Test
    public void handleAdoptInfoSendsDetailsMenuTest() {
        long chatId = 123L;

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.CAT);

        adoptInfoService.handleAdoptInfo(chatId);

        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture());
        var allMessages = sendMessageCaptor.getAllValues();

        SendMessage menuMessage = allMessages.get(1);
        Object replyMarkup = menuMessage.getParameters().get("reply_markup");
        assert replyMarkup != null;
    }

    @Test
    public void handleAdoptCallback_DocumentsSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_DOCS", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Документы для выдачи питомца");
    }

    @Test
    public void handleAdoptCallback_TransportSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_TRANSPORT", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Рекомендации по транспортировке");
    }

    @Test
    public void handleAdoptCallback_MeetTipsSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_MEET_TIPS", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Как правильно познакомиться с животным");
    }

    @Test
    public void handleAdoptCallback_HomePuppySendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_HOME_PUPPY", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Обустройство дома для щенка/котёнка");
    }

    @Test
    public void handleAdoptCallback_HomeAdultSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_HOME_ADULT", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Обустройство дома для взрослого животного");
    }

    @Test
    public void handleAdoptCallback_HomeSpecialSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_HOME_SPECIAL", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Обустройство дома для животного с ОВЗ");
    }

    @Test
    public void handleAdoptCallback_K9TipsSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_K9_TIPS", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Советы кинолога по первичному общению с собакой");
    }

    @Test
    public void handleAdoptCallback_K9ContactsSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_K9_CONTACTS", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Проверенные кинологи (Москва)");
    }

    @Test
    public void handleAdoptCallback_RejectionReasonsSendsTextTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("ADOPT_REJECTION_REASONS", chatId);

        verify(telegramBot).execute(sendMessageCaptor.capture());
        String text = sendMessageCaptor.getValue().getParameters().get("text").toString();

        assert text.contains("Причины, по которым могут отказать в выдаче собаки");
    }

    @Test
    public void handleAdoptCallback_UnknownDataDoesNotSendMessageTest() {
        long chatId = 123L;

        adoptInfoService.handleAdoptCallback("UNKNOWN", chatId);

        verify(telegramBot, never()).execute(any(SendMessage.class));
    }
}