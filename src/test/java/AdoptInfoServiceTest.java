import com.animalShelterBot.service.AdoptInfoService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdoptInfoServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private AdoptInfoService adoptInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleAdoptInfo_shouldSendProperMessageWithMarkdown() {
        // Given
        long chatId = 123456789L;
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        // When
        adoptInfoService.handleAdoptInfo(chatId);

        // Then
        verify(telegramBot, times(1)).execute(captor.capture());
        SendMessage capturedMessage = captor.getValue();

        String expectedText = "🐾 *Как взять животное:*\n" +
                "1️⃣ Заполните анкету потенциального хозяина\n" +
                "2️⃣ Дождитесь звонка волонтёра\n" +
                "3️⃣ Приезжайте знакомиться с питомцем\n" +
                "4️⃣ Подпишите договор и заберите друга!";

        assertThat(capturedMessage.getParameters().get("chat_id")).isEqualTo(chatId);
        assertThat(capturedMessage.getParameters().get("text")).isEqualTo(expectedText);
        assertThat(capturedMessage.getParameters().get("parse_mode")).isEqualTo("Markdown");
    }
}