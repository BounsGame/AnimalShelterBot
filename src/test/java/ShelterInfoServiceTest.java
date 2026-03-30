import com.animalShelterBot.model.AnimalType;
import com.animalShelterBot.repository.ContactsRepository;
import com.animalShelterBot.service.ShelterInfoService;
import com.animalShelterBot.service.UserSessionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterInfoServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private ContactsRepository contactsRepository;

    @InjectMocks
    private ShelterInfoService shelterInfoService;

    @Captor
    private ArgumentCaptor<SendMessage> messageCaptor;

    private long chatId;

    @BeforeEach
    void setup() {
        long chatId = 1L;
    }

    @Test
    public void handleShelterInfoTest() {
        shelterInfoService.handleShelterInfo(chatId);

        verify(telegramBot).execute(messageCaptor.capture());

        SendMessage sendMessage = messageCaptor.getValue();
        InlineKeyboardMarkup inlineKeyboardMarkup = (InlineKeyboardMarkup) sendMessage.getParameters().get("reply_markup");
        List<InlineKeyboardButton> buttons = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(0)).toList();
        List<InlineKeyboardButton> buttons1 = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(1)).toList();
        List<InlineKeyboardButton> buttons2 = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(2)).toList();
        List<InlineKeyboardButton> buttons3 = Arrays.stream(Arrays.stream(inlineKeyboardMarkup.inlineKeyboard()).toList().get(3)).toList();

        assert (buttons.get(0).callbackData().contains("SHELTER_DESCRIPTION"));
        assert (buttons.get(1).callbackData().contains("SHELTER_SCHEDULE"));
        assert (buttons1.get(0).callbackData().contains("SHELTER_ADDRESS"));
        assert (buttons1.get(1).callbackData().contains("SECURITY_CONTACTS"));
        assert (buttons2.get(0).callbackData().contains("SAFETY_RULES"));
        assert (buttons2.get(1).callbackData().contains("USER_CONTACTS"));
        assert (buttons3.get(0).callbackData().contains("MENU_VOLUNTEER"));
    }

    @Test
    public void handleShelterInfoTest_shouldSendMessage_whenCallBackShelterDescription() {
        String callbackData = "SHELTER_DESCRIPTION";

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.DOG);
        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    public void handleShelterInfoTest_shouldSendMessage_whenCallBackShelterSchedule() {
        String callbackData = "SHELTER_SCHEDULE";

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.DOG);
        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    public void handleShelterInfoTest_shouldSendAddressAndImage_whenCallBackAddress() {
        String callbackData = "SHELTER_ADDRESS";

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.DOG);
        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
        verify(telegramBot).execute(any(SendPhoto.class));
    }

    @Test
    public void handleShelterInfoTest_shouldSendMessage_whenCallBackSecurityContacts() {
        String callbackData = "SECURITY_CONTACTS";

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.CAT);
        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    public void handleShelterInfoTest_shouldSendMessage_whenCallBackSafetyRules() {
        String callbackData = "SAFETY_RULES";

        when(userSessionService.getShelterType(chatId)).thenReturn(AnimalType.CAT);
        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    public void saveUserContactsTest_shouldSaveContactsAndSendMessage_whenContactsProvided() {
        String contactInfo = "test@mail.com";
        shelterInfoService.saveUserContacts(chatId, contactInfo);

        verify(contactsRepository).save(argThat(contact ->
                contact.getChatId() == chatId &&
                        contact.getContacts().equals(contactInfo.trim())
        ));
        verify(telegramBot).execute(any(SendMessage.class));
        verify(userSessionService).setStateInShelterInfoMenu(chatId);
    }

    @Test
    public void saveUserContactsTest_shouldSendErrorMessageAndNotSaveContacts_whenContactsIsNull() {
        String contactInfo = null;
        shelterInfoService.saveUserContacts(chatId, contactInfo);

        verify(telegramBot).execute(any(SendMessage.class));
        verify(contactsRepository, never()).save(any());
        verify(userSessionService, never()).setStateInShelterInfoMenu(chatId);
    }

    @Test
    public void saveUserContactsTest_shouldSendErrorMessageAndNotSaveContacts_whenContactsIsEmpty() {
        String contactInfo = "   ";
        shelterInfoService.saveUserContacts(chatId, contactInfo);

        verify(telegramBot).execute(any(SendMessage.class));
        verify(contactsRepository, never()).save(any());
        verify(userSessionService, never()).setStateInShelterInfoMenu(chatId);
    }

    @Test
    public void handleShelterInfoTest_shouldSendErrorMessage_whenUnknownCallback() {
        String callbackData = "SOME_BUTTON";

        shelterInfoService.handleShelterInfoMenu(chatId, callbackData);

        verify(telegramBot).execute(any(SendMessage.class));
    }
}