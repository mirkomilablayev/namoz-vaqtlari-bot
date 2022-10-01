package com.example.namoz_vaqtlari_bot.controller;

import com.example.namoz_vaqtlari_bot.ConflictException;
import com.example.namoz_vaqtlari_bot.configuration.BotConfiguration;
import com.example.namoz_vaqtlari_bot.dto.getChatMember.GetChatMemberDto;
import com.example.namoz_vaqtlari_bot.entity.CompulsoryChannel;
import com.example.namoz_vaqtlari_bot.entity.User;
import com.example.namoz_vaqtlari_bot.repository.CompulsoryChannelRepo;
import com.example.namoz_vaqtlari_bot.repository.RoleRepository;
import com.example.namoz_vaqtlari_bot.repository.UserRepository;
import com.example.namoz_vaqtlari_bot.util.Constants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MyBotService extends TelegramLongPollingBot {


    private final BotConfiguration botConfiguration;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompulsoryChannelRepo compulsoryChannelRepo;


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
        if (isJoinedToChannels || all.size() == 0) {
            Boolean isOnUserPage = user.getIsOnUserPage();
            if (isOnUserPage) {
                userPageMethod(update, user, sendMessage);
                sendMessage.setText("USER");
            } else {
                sendMessage.setText("ADMIN");
                adminPageMethod(update, user, sendMessage);
            }
        } else {
            sendCompulsoryChannels(sendMessage, all);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCompulsoryChannels(SendMessage sendMessage, List<CompulsoryChannel> all) {
        sendMessage.setText("Assalomu alaykum botdan foydalanish uchun quyidagi kanallarga obuna bo'lishingiz kerak\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (CompulsoryChannel compulsoryChannel : all) {
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(compulsoryChannel.getChannel_name());
            inlineKeyboardButton.setUrl("https://t.me//" + compulsoryChannel.getChannel_username());
            rowInLine.add(inlineKeyboardButton);
            rowsInLine.add(rowInLine);
            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("🔄Tekshirish"));
        keyboardRows.add(firstRow);
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    private String getText(Update update) {
        String text = "";
        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getMessage().getText();
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
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        String chat_username = "@mirkomil_dev";
        String realId = user.getRealId();
        String url_ = "https://api.telegram.org/bot" + token + "/getChatMember?chat_id=" + chat_username + "&user_id=" + realId;

        URL url = null;
        try {
            url = new URL(url_);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            GetChatMemberDto tg_user = new Gson().fromJson(String.valueOf(responseContent), GetChatMemberDto.class);
            return tg_user.getResult().getStatus().equals("creator") ||
                    tg_user.getResult().getStatus().equals("member") ||
                    tg_user.getResult().getStatus().equals("administrator");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void userPageMethod(Update update, User user, SendMessage sendMessage) {
        sendMessage.setText("Siz User pagedasiz");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("⏰Shu hafta Na'moz Vaqtlari"));
        keyboardRows.add(firstRow);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton("⚙️Na'moz vaqti sozlamalari"));
        if (user.getRole().size() > 0) {
            secondRow.add(new KeyboardButton("\uD83D\uDC64Admin Page"));
        }
        keyboardRows.add(secondRow);
        markup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(markup);


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
