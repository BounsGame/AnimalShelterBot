import com.animalShelterBot.service.ReportRequestService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReportRequestServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private ReportRequestService reportRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleReportRequest_shouldSetAwaitingReportStateAndSendMessage() {
        long chatId = 123456789L;
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        String expectedText = "📝 Отправьте фото и короткий текст о том, как дела у вашего питомца.";

        reportRequestService.handleReportRequest(chatId);

        verify(userSessionService, times(1)).setStateAwaitingReport(chatId);

        verify(telegramBot, times(1)).execute(captor.capture());
        SendMessage capturedMessage = captor.getValue();

        assertThat(capturedMessage.getParameters().get("chat_id")).isEqualTo(chatId);
        assertThat(capturedMessage.getParameters().get("text")).isEqualTo(expectedText);
    }
}