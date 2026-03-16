import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.service.StartHandlerService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartHandlerServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserSessionService userSessionService;

    @Spy
    @InjectMocks
    private StartHandlerService startHandlerService;

    @Captor
    private ArgumentCaptor<SendMessage> messageCaptor;

    @Test
    public void sendShelterChoiceTest() {
        long chatId = 123L;

        startHandlerService.sendShelterChoice(chatId);

        verify(telegramBot).execute(messageCaptor.capture());

        SendMessage sendMessage = messageCaptor.getValue();

        assertNotNull(sendMessage.getParameters().get("text"));

        InlineKeyboardMarkup inlineKeyboardMarkup = (InlineKeyboardMarkup) sendMessage.getParameters().get("reply_markup");
        List<InlineKeyboardButton[]> buttons = Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList();
        List<InlineKeyboardButton> buttons2 = Arrays.stream(buttons.get(0)).toList();
        InlineKeyboardButton button1 = buttons2.get(0);
        InlineKeyboardButton button2 = buttons2.get(1);
        assertEquals("CAT", button1.callbackData());
        assertEquals("DOG", button2.callbackData());
    }

    @Test
    public void getShelterChoiceDogTest() {
        String cat = "DOG";
        long chatId = 123L;

        doNothing().when(startHandlerService).sendMainMenu(anyLong());

        startHandlerService.getShelterChoice(cat, chatId);

        verify(userSessionService).setShelterTypeDog(any());
        verify(userSessionService).setStateInMainMenu(any());

        verify(telegramBot).execute(messageCaptor.capture());

        String text = (String) messageCaptor.getValue().getParameters().get("text");
        assert (text.contains("собак"));
    }

    @Test
    public void getShelterChoiceCatTest() {
        String cat = "CAT";
        long chatId = 123L;

        doNothing().when(startHandlerService).sendMainMenu(anyLong());

        startHandlerService.getShelterChoice(cat, chatId);

        verify(userSessionService).setShelterTypeCat(any());
        verify(userSessionService).setStateInMainMenu(any());

        verify(telegramBot).execute(messageCaptor.capture());

        String text = (String) messageCaptor.getValue().getParameters().get("text");
        assert (text.contains("кошек"));
    }

    @Test
    public void sendMainMenuTest() {
        long chatId = 123L;

        startHandlerService.sendMainMenu(chatId);

        verify(telegramBot).execute(messageCaptor.capture());

        SendMessage sendMessage = messageCaptor.getValue();
        InlineKeyboardMarkup inlineKeyboardMarkup = (InlineKeyboardMarkup) sendMessage.getParameters().get("reply_markup");
        List<InlineKeyboardButton> buttons = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(0)).toList();
        List<InlineKeyboardButton> buttons1 = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(1)).toList();

        assert (buttons.get(0).callbackData().contains("MENU_INFO"));
        assert (buttons.get(1).callbackData().contains("MENU_ADOPT"));
        assert (buttons1.get(0).callbackData().contains("MENU_REPORT"));
        assert (buttons1.get(1).callbackData().contains("MENU_VOLUNTEER"));
    }

    @Test
    public void StartTest(){
        long chatId = 123L;

        doNothing().when(startHandlerService).sendShelterChoice(anyLong());

        startHandlerService.handleStart(chatId);

        verify(telegramBot).execute(messageCaptor.capture());
        SendMessage sendMessage = messageCaptor.getValue();
        assertNotNull(sendMessage.getParameters().get("text"));
    }
}
