package com.example.namoz_vaqtlari_bot.controller;

import com.example.namoz_vaqtlari_bot.ConflictException;
import com.example.namoz_vaqtlari_bot.configuration.BotConfiguration;
import com.example.namoz_vaqtlari_bot.dto.currency.CurrencyDto;
import com.example.namoz_vaqtlari_bot.dto.getChatMember.GetChatMemberDto;
import com.example.namoz_vaqtlari_bot.dto.namoz_vaqti_dto.TimeResponseDto;
import com.example.namoz_vaqtlari_bot.entity.CompulsoryChannel;
import com.example.namoz_vaqtlari_bot.entity.Region;
import com.example.namoz_vaqtlari_bot.entity.User;
import com.example.namoz_vaqtlari_bot.entity.UserRole;
import com.example.namoz_vaqtlari_bot.repository.CompulsoryChannelRepo;
import com.example.namoz_vaqtlari_bot.repository.RegionRepo;
import com.example.namoz_vaqtlari_bot.repository.RoleRepository;
import com.example.namoz_vaqtlari_bot.repository.UserRepository;
import com.example.namoz_vaqtlari_bot.util.Constants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@RequiredArgsConstructor
@Component
public class MyBotService extends TelegramLongPollingBot {


    private final BotConfiguration botConfiguration;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompulsoryChannelRepo compulsoryChannelRepo;
    private final RegionRepo regionRepo;

    private final String[] weekdays = {"Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba", "Yakshanba"};

    @Override
    public String getBotUsername() {
        return this.botConfiguration.getUsername();
    }

    @Override
    public String getBotToken() {
        return this.botConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = getChatId(update);
        String text = getText(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);


        User user = getCurrentUser(chatId, update);
        Boolean isJoinedToChannels = getChannelMembers(user);
        List<CompulsoryChannel> all = compulsoryChannelRepo.findAll();
        if (user.getRole().size() == 1 || user.getIsOnUserPage()) {

            if (isJoinedToChannels || all.size() == 0) {
                userPageMethod(update, user, sendMessage, text);
            } else {
                sendCompulsoryChannels(sendMessage, all);
            }
        } else {
            adminPageMethod(update, user, sendMessage);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCompulsoryChannels(SendMessage sendMessage, List<CompulsoryChannel> all) {
        sendMessage.setText("Iltimos majburiy kanallarga a'zo bo'ling\nkanallarga a'zo bo'lib /start 👈 ");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (CompulsoryChannel compulsoryChannel : all) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(compulsoryChannel.getResult().getTitle());
            inlineKeyboardButton.setUrl("https://t.me//" + compulsoryChannel.getResult().getUsername());
            rowInLine.add(inlineKeyboardButton);
            rowsInLine.add(rowInLine);
            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    private String getText(Update update) {
        String text = "";
        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData();
        } else if (update.hasChannelPost()) {
            text = update.getChannelPost().getText();
        } else if (update.hasMessage()) {
            text = update.getMessage().getText();
        } else {
            throw new ConflictException();
        }
        return text;
    }


    private String getChatId(Update update) {
        String chatId = "";
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId().toString();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
        } else {
            throw new ConflictException();
        }
        return chatId;
    }

    @Value("${bot.token}")
    private String token;

    private Boolean getChannelMembers(User user) {
        String chat_username = "@djhdjfhdhjfdhfdjhfj";
        String realId = user.getRealId();
        String url_ = "https://api.telegram.org/bot" + token + "/getChatMember?chat_id=" + chat_username + "&user_id=" + realId;

        try {
            GetChatMemberDto tg_user = new RestTemplate().getForObject(url_, GetChatMemberDto.class);
            assert tg_user != null;
            return checkUserStatusForChannel(tg_user.getResult().getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean checkUserStatusForChannel(String status) {
        String[] statuses = {"creator", "administrator", "member"};
        for (String s : statuses) {
            if (status.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    private User getCurrentUser(String chatId, Update update) {
        Optional<User> userOptional = userRepository.findByChatIdAndIsActive(chatId, true);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User user = new User();
            String userName = update.getMessage().getFrom().getUserName();
            if (userName != null && userName.equals("mirkomil_ablayev")) {
                user.setRole(new ArrayList<>(roleRepository.findAll()));
            } else {
                user.setRole(new ArrayList<>(Collections.singletonList(roleRepository.findByRoleName(Constants.USER).get())));
            }
            user.setChatId(chatId);
            user.setIsOnUserPage(true);
            user.setFullName(update.getMessage().getFrom().getFirstName());
            user.setRealId(update.getMessage().getFrom().getId().toString());
            return userRepository.save(user);
        }
    }

    private void userPageMethod(Update update, User user, SendMessage sendMessage, String text) {
        if (isRegionSetUp(null, text)) {
            user.setRegion(text);
            user = userRepository.save(user);
            sendMessage.setText("Sizning mintaqangiz sozlandi. Sizga " + user.getRegion() + " uchun namoz vaqtlari berib boriladi");
            userButtons(sendMessage, user);
        }
        if (!isRegionSetUp(user, null)) {
            sendRegions(sendMessage, regionRepo.findAll());
        }
        if (text.equalsIgnoreCase("/start")) {
            sendMessage.setText("Asosiy Menyu");
            userButtons(sendMessage, user);
        } else if (text.equalsIgnoreCase(Constants.userButton2)) {
            sendThisMonthTimes(sendMessage, user,null);
        } else if (isExistWeekDays(text)) {
            sendThisMonthTimes(sendMessage, user,text);
        } else {
            sendMessage.setText("Xato Buyruq kiritildi");
        }
    }


    private Boolean isExistWeekDays(String weekDay) {
        for (String weekday : weekdays) {
            if (weekday.equals(weekDay)) {
                return true;
            }
        }
        return false;
    }

    private void sendThisMonthTimes(SendMessage sendMessage, User user, String weekDay) {
        String message = "Shu Xafta Uchun Na'moz Vaqtlari\n\n\uD83C\uDF0D Mintaqa >> "+user.getRegion()+"\n\n";
        List<TimeResponseDto> body = getTimes(user);
        String currencyMessage = "\uD83D\uDCB8 Valyutalar Kurslari: \n\n";
        String[] strings = {"USD", "RUB", "EUR"};
        for (String string : strings) {
            CurrencyDto todayCurrency = getTodayCurrency(string);
            assert todayCurrency != null;
            currencyMessage += todayCurrency.getCcyNm_UZ() + " >>> " + todayCurrency.getRate() + "\n";
        }


        if (weekDay == null) {
            sendMessage.setText("Qaysi Hafta kuni uchun vaqtlarni ko'rmoqchisiz ?" + "\n\n" + message);
            sendWeekDays(sendMessage, body);
        } else {
            for (TimeResponseDto timeResponseDto : body) {
                if (timeResponseDto.getWeekday().equals(weekDay)) {
                    message += "⏰ Bomdod | " + timeResponseDto.getTimes().getTong_saharlik() + "\n"
                            + "⏰ Quyosh | " + timeResponseDto.getTimes().getQuyosh() + "\n"
                            + "⏰ Peshin | " + timeResponseDto.getTimes().getPeshin() + "\n"
                            + "⏰ Asr | " + timeResponseDto.getTimes().getAsr() + "\n"
                            + "⏰ Shom | " + timeResponseDto.getTimes().getShom_iftor() + "\n"
                            + "⏰ Hufton | " + timeResponseDto.getTimes().getHufton() + "\n";
                    break;
                }
            }
            sendMessage.setText(currencyMessage + "\n" + message);
        }

    }

    private static List<TimeResponseDto> getTimes(User user) {
        String url = "https://islomapi.uz/api/present/week?region=" + user.getRegion();
        return new RestTemplate().exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TimeResponseDto>>() {
                }
        ).getBody();
    }

    private CurrencyDto getTodayCurrency(String currencyType) {
        String url = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/";
        CurrencyDto[] test = test(url);
        assert test != null;
        for (CurrencyDto currency : test) {
            String ccy = currency.getCcy();
            if (ccy.equalsIgnoreCase(currencyType)) {
                return currency;
            }
        }
        return null;
    }


    public CurrencyDto[] test(String theUrl) {
        StringBuilder content = new StringBuilder();
        // Use try and catch to avoid the exceptions
        try {
            URL url = new URL(theUrl); // creating a url object
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object

            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            System.out.println(bufferedReader);
            CurrencyDto[] currencyDtos = new Gson().fromJson(bufferedReader, CurrencyDto[].class);
            bufferedReader.close();
            return currencyDtos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void userButtons(SendMessage sendMessage, User user) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton(Constants.userButton1));
        keyboardRows.add(firstRow);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton(Constants.userButton2));
        keyboardRows.add(secondRow);
        if (checkUserRoleForAdmin(user)) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(Constants.userButton3));
            keyboardRows.add(keyboardRow);
        }
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
    }


    private Boolean checkUserRoleForAdmin(User user) {
        List<UserRole> role = user.getRole();
        for (UserRole userRole : role) {
            if (userRole.getRoleName().equalsIgnoreCase(Constants.ADMIN)) {
                return true;
            }
        }

        return false;
    }

    private boolean isRegionSetUp(User user, String text) {
        List<Region> all = regionRepo.findAll();
        if (text == null) {
            for (Region region : all) {
                if (user != null && user.getRegion() != null && user.getRegion().equalsIgnoreCase(region.getName())) {
                    return true;
                }
            }
        }

        if (user == null) {
            for (Region region : all) {
                assert text != null;
                if (text.equalsIgnoreCase(region.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendWeekDays(SendMessage sendMessage, List<TimeResponseDto> all) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (TimeResponseDto compulsoryChannel : all) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(compulsoryChannel.getWeekday());
            inlineKeyboardButton.setCallbackData(compulsoryChannel.getWeekday());
            rowInLine.add(inlineKeyboardButton);
            rowsInLine.add(rowInLine);
            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    private void sendRegions(SendMessage sendMessage, List<Region> all) {
        System.out.println(all.size());
        sendMessage.setText("Siz o'zingizni viloyatingizni tanlab oling!!");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (Region compulsoryChannel : all) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(compulsoryChannel.getReg_name());
            inlineKeyboardButton.setCallbackData(compulsoryChannel.getName());
            rowInLine.add(inlineKeyboardButton);
            rowsInLine.add(rowInLine);
            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }


    private void adminPageMethod(Update update, User user, SendMessage sendMessage) {
        String stepOn = update.getMessage().getText();

        sendMessage.setText("Siz Adminstrator pagedasiz");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton(Constants.adminStep2));
        firstRow.add(new KeyboardButton(Constants.adminStep3));
        keyboardRows.add(firstRow);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton(Constants.adminStep4));
        secondRow.add(new KeyboardButton(Constants.adminStep6));
        keyboardRows.add(secondRow);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(Constants.adminStep5));
        keyboardRows.add(keyboardRow);
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        if (stepOn.equals(Constants.adminStep1)) {
        } else if (stepOn.equals(Constants.adminStep2)) {
            sendMessage.setText(Constants.adminStep2);
        } else if (stepOn.equals(Constants.adminStep3)) {
            sendMessage.setText(Constants.adminStep3);
        } else if (stepOn.equals(Constants.adminStep4)) {
            sendMessage.setText(Constants.adminStep4);

        } else if (stepOn.equals(Constants.adminStep5)) {
            sendMessage.setText(Constants.adminStep5);
        } else if (stepOn.equals(Constants.adminStep6)) {
            sendMessage.setText(Constants.adminStep6);
        } else {
            sendMessage.setText("Wrong step found");
        }
    }

}
