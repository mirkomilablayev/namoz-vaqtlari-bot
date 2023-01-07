package com.example.namoz_vaqtlari_bot.service;

import com.example.namoz_vaqtlari_bot.configuration.BotProperties;
import com.example.namoz_vaqtlari_bot.entity.CurrencyDTO;
import com.example.namoz_vaqtlari_bot.entity.CurrencyEntity;
import com.example.namoz_vaqtlari_bot.entity.Region;
import com.example.namoz_vaqtlari_bot.entity.User;
import com.example.namoz_vaqtlari_bot.repository.CurrencyEntityRepo;
import com.example.namoz_vaqtlari_bot.repository.RegionRepository;
import com.example.namoz_vaqtlari_bot.repository.UserRepository;
import com.example.namoz_vaqtlari_bot.util.BotMethodsUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MyBotService extends TelegramLongPollingBot {


    private final BotMethodsUtil methodsUtil;
    private final BotProperties botProperties;
    private final UserRepository userRepository;

    private final CurrencyEntityRepo currencyEntityRepo;
    private final String[] weekdays = {"Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba", "Yakshanba"};
    private final RegionRepository regionRepository;

    @Override
    public String getBotUsername() {
        return this.botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return this.botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = methodsUtil.getChatId(update);
        User currentUser = getCurrentUser(chatId, update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (currentUser.getIsAdmin()) {
            adminPage(currentUser, update, chatId, sendMessage);
        } else {
            userPage(currentUser, update, chatId, sendMessage);
        }


        executeSendMessage(sendMessage);
    }

    private void userPage(User currentUser, Update update, String chatId, SendMessage sendMessage) {
        if (currentUser.getRegionId() == null) {
            if (currentUser.getIsSendRegionList()) {
                if (update.hasCallbackQuery()) {
                    String data = update.getCallbackQuery().getData();
                    currentUser.setRegionId(Long.valueOf(data));
                    userRepository.save(currentUser);
                    Region region = regionRepository.findById(Long.valueOf(data)).orElse(new Region());
                    sendMessage.setText(region.getRegionShowName()+" Muvofaqqiyatli saqlandi, endi sizga ushbu viloyatning na'moz vaqtlari ko'rsatiladi");
                    return;
                }
                sendMessage.setText(getTodayCurrencies() + "\n");
            } else {
                currentUser.setIsSendRegionList(true);
                userRepository.save(currentUser);
                sendRegions(sendMessage, regionRepository.findAll());
            }
            return;
        }
    }

    public String getInlineKeyboardValue(Update update) {
        return "";
    }

    private void adminPage(User currentUser, Update update, String chatId, SendMessage sendMessage) {

    }

    private void executeSendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public User getCurrentUser(String chatId, Update update) {
        Optional<User> userOptional = userRepository.findByChatId(chatId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User user = new User();
            user.setChatId(chatId);
            user.setIsAdmin(false);
            user.setName(update.getMessage().getFrom().getFirstName());
            return userRepository.save(user);
        }
    }
//
//    private void sendCompulsoryChannels(SendMessage sendMessage, List<CompulsoryChannel> all) {
//        sendMessage.setText("Iltimos majburiy kanallarga a'zo bo'ling\nkanallarga a'zo bo'lib /start 👈 ");
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
//        for (CompulsoryChannel compulsoryChannel : all) {
//            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
//            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//            inlineKeyboardButton.setText(compulsoryChannel.getResult().getTitle());
//            inlineKeyboardButton.setUrl("https://t.me//" + compulsoryChannel.getResult().getUsername());
//            rowInLine.add(inlineKeyboardButton);
//            rowsInLine.add(rowInLine);
//            inlineKeyboardMarkup.setKeyboard(rowsInLine);
//            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//        }
//        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
//        markup.setResizeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        markup.setKeyboard(keyboardRows);
//        sendMessage.setReplyMarkup(markup);
//        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//    }
//
//    private String getText(Update update) {
//        String text = "";
//        if (update.hasCallbackQuery()) {
//            text = update.getCallbackQuery().getData();
//        } else if (update.hasChannelPost()) {
//            text = update.getChannelPost().getText();
//        } else if (update.hasMessage()) {
//            text = update.getMessage().getText();
//        } else {
//            throw new ConflictException();
//        }
//        return text;
//    }
//
//
//    private String getChatId(Update update) {
//        String chatId = "";
//        if (update.hasCallbackQuery()) {
//            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
//        } else if (update.hasChannelPost()) {
//            chatId = update.getChannelPost().getChatId().toString();
//        } else if (update.hasMessage()) {
//            chatId = update.getMessage().getChatId().toString();
//        } else {
//            throw new ConflictException();
//        }
//        return chatId;
//    }
//
//    @Value("${bot.token}")
//    private String token;
//
//    private Boolean getChannelMembers(User user) {
//        String chat_username = "@djhdjfhdhjfdhfdjhfj";
//        String realId = user.getRealId();
//        String url_ = "https://api.telegram.org/bot" + token + "/getChatMember?chat_id=" + chat_username + "&user_id=" + realId;
//
//        try {
//            GetChatMemberDto tg_user = new RestTemplate().getForObject(url_, GetChatMemberDto.class);
//            assert tg_user != null;
//            return checkUserStatusForChannel(tg_user.getResult().getStatus());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Boolean checkUserStatusForChannel(String status) {
//        String[] statuses = {"creator", "administrator", "member"};
//        for (String s : statuses) {
//            if (status.equalsIgnoreCase(s)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    private User getCurrentUser(String chatId, Update update) {
//        Optional<User> userOptional = userRepository.findByChatIdAndIsActive(chatId, true);
//        if (userOptional.isPresent()) {
//            return userOptional.get();
//        } else {
//            User user = new User();
//            String userName = update.getMessage().getFrom().getUserName();
//            if (userName != null && userName.equals("mirkomil_ablayev")) {
//                user.setRole(new ArrayList<>(roleRepository.findAll()));
//            } else {
//                user.setRole(new ArrayList<>(Collections.singletonList(roleRepository.findByRoleName(Constants.USER).get())));
//            }
//            user.setChatId(chatId);
//            user.setIsOnUserPage(true);
//            user.setFullName(update.getMessage().getFrom().getFirstName());
//            user.setRealId(update.getMessage().getFrom().getId().toString());
//            return userRepository.save(user);
//        }
//    }
//
//    private void userPageMethod(Update update, User user, SendMessage sendMessage, String text) {
//        if (isRegionSetUp(null, text)) {
//            user.setRegion(text);
//            user = userRepository.save(user);
//            sendMessage.setText("Sizning mintaqangiz sozlandi. Sizga " + user.getRegion() + " uchun namoz vaqtlari berib boriladi");
//            userButtons(sendMessage, user);
//        }
//        if (!isRegionSetUp(user, null)) {
//            sendRegions(sendMessage, regionRepo.findAll());
//        }
//        if (text.equalsIgnoreCase("/start")) {
//            sendMessage.setText("Asosiy Menyu");
//            userButtons(sendMessage, user);
//        } else if (text.equalsIgnoreCase(Constants.userButton2)) {
//            sendThisMonthTimes(sendMessage, user, null);
//        } else if (isExistWeekDays(text)) {
//            sendThisMonthTimes(sendMessage, user, text);
//        } else {
//            sendMessage.setText("Xato Buyruq kiritildi");
//        }
//    }
//
//
//    private Boolean isExistWeekDays(String weekDay) {
//        for (String weekday : weekdays) {
//            if (weekday.equals(weekDay)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void sendThisMonthTimes(SendMessage sendMessage, User user, String weekDay) {
//        String message = "Shu Xafta Uchun Na'moz Vaqtlari\n\n\uD83C\uDF0D Mintaqa >> " + user.getRegion() + "\n\n";
//        List<TimeResponseDto> body = getTimes(user);
//        String currencyMessage = "\uD83D\uDCB8 Valyutalar Kurslari: \n\n";
//        String[] strings = {"USD", "RUB", "EUR"};
//        for (String string : strings) {
//            CurrencyDto todayCurrency = getTodayCurrency(string);
//            assert todayCurrency != null;
//            currencyMessage += todayCurrency.getCcyNm_UZ() + " >>> " + todayCurrency.getRate() + "\n";
//        }
//
//
//        if (weekDay == null) {
//            sendMessage.setText("Qaysi Hafta kuni uchun vaqtlarni ko'rmoqchisiz ?" + "\n\n" + message);
//            sendWeekDays(sendMessage, body);
//        } else {
//            for (TimeResponseDto timeResponseDto : body) {
//                if (timeResponseDto.getWeekday().equals(weekDay)) {
//                    message += "⏰ Bomdod | " + timeResponseDto.getTimes().getTong_saharlik() + "\n"
//                            + "⏰ Quyosh | " + timeResponseDto.getTimes().getQuyosh() + "\n"
//                            + "⏰ Peshin | " + timeResponseDto.getTimes().getPeshin() + "\n"
//                            + "⏰ Asr | " + timeResponseDto.getTimes().getAsr() + "\n"
//                            + "⏰ Shom | " + timeResponseDto.getTimes().getShom_iftor() + "\n"
//                            + "⏰ Hufton | " + timeResponseDto.getTimes().getHufton() + "\n";
//                    break;
//                }
//            }
//            sendMessage.setText(currencyMessage + "\n" + message);
//        }
//
//    }
//
//    private static List<TimeResponseDto> getTimes(User user) {
//        String url = "https://islomapi.uz/api/present/week?region=" + user.getRegion();
//        return new RestTemplate().exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<TimeResponseDto>>() {
//                }
//        ).getBody();
//    }
//


    private String getTodayCurrencies() {
        CurrencyEntity usd = getOrUpdate("USD");
        CurrencyEntity rub = getOrUpdate("RUB");
        CurrencyEntity eur = getOrUpdate("EUR");
        return "Usd: " + usd.getCurrencyRate() + "<> Eur: " + eur.getCurrencyRate() + "<> Rub: " + rub.getCurrencyRate();
    }


    private CurrencyEntity getOrUpdate(String ccy) {
        Optional<CurrencyEntity> currencyEntityOptional = currencyEntityRepo.findByCcyName(ccy);
        if (currencyEntityOptional.isPresent()) {
            CurrencyEntity currencyEntity = currencyEntityOptional.get();
            LocalDate now = LocalDate.now();
            String date = "" + now.getDayOfMonth() + "." + now.getMonthValue() + "." + now.getYear();
            if (currencyEntity.getCurrencyDate().equals(date)) {
                return currencyEntity;
            } else {
                CurrencyDTO todayCurrencyDTO = getTodayCurrency(ccy);
                assert todayCurrencyDTO != null;
                currencyEntity.setCurrencyRate(todayCurrencyDTO.getRate());
                currencyEntity.setCurrencyDate(todayCurrencyDTO.getDate());
                currencyEntity.setCcyName(todayCurrencyDTO.getCcy());
                return currencyEntityRepo.save(currencyEntity);
            }
        }
        CurrencyDTO todayCurrency = getTodayCurrency(ccy);
        assert todayCurrency != null;
        return currencyEntityRepo.save(new CurrencyEntity(todayCurrency.getCcy(), todayCurrency.getRate(), todayCurrency.getDate()));
    }

    private CurrencyDTO getTodayCurrency(String currencyType) {
        String url = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/";
        CurrencyDTO[] test = getCurrencies(url);
        assert test != null;
        for (CurrencyDTO currencyDTO : test) {
            String ccy = currencyDTO.getCcy();
            if (ccy.equalsIgnoreCase(currencyType)) {
                return currencyDTO;
            }
        }
        return null;
    }

    //
//
    public CurrencyDTO[] getCurrencies(String theUrl) {
        StringBuilder content = new StringBuilder();
        // Use try and catch to avoid the exceptions
        try {
            URL url = new URL(theUrl); // creating a url object
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object

            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            System.out.println(bufferedReader);
            CurrencyDTO[] currencies = new Gson().fromJson(bufferedReader, CurrencyDTO[].class);
            bufferedReader.close();
            return currencies;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //
//
//    private void userButtons(SendMessage sendMessage, User user) {
//        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
//        markup.setResizeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        KeyboardRow firstRow = new KeyboardRow();
//        firstRow.add(new KeyboardButton(Constants.userButton1));
//        keyboardRows.add(firstRow);
//        KeyboardRow secondRow = new KeyboardRow();
//        secondRow.add(new KeyboardButton(Constants.userButton2));
//        keyboardRows.add(secondRow);
//        if (checkUserRoleForAdmin(user)) {
//            KeyboardRow keyboardRow = new KeyboardRow();
//            keyboardRow.add(new KeyboardButton(Constants.userButton3));
//            keyboardRows.add(keyboardRow);
//        }
//        markup.setKeyboard(keyboardRows);
//        sendMessage.setReplyMarkup(markup);
//    }
//
//
//    private Boolean checkUserRoleForAdmin(User user) {
//        List<UserRole> role = user.getRole();
//        for (UserRole userRole : role) {
//            if (userRole.getRoleName().equalsIgnoreCase(Constants.ADMIN)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean isRegionSetUp(User user, String text) {
//        List<Region> all = regionRepo.findAll();
//        if (text == null) {
//            for (Region region : all) {
//                if (user != null && user.getRegion() != null && user.getRegion().equalsIgnoreCase(region.getName())) {
//                    return true;
//                }
//            }
//        }
//
//        if (user == null) {
//            for (Region region : all) {
//                assert text != null;
//                if (text.equalsIgnoreCase(region.getName())) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private void sendWeekDays(SendMessage sendMessage, List<TimeResponseDto> all) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
//        for (TimeResponseDto compulsoryChannel : all) {
//            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
//            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//            inlineKeyboardButton.setText(compulsoryChannel.getWeekday());
//            inlineKeyboardButton.setCallbackData(compulsoryChannel.getWeekday());
//            rowInLine.add(inlineKeyboardButton);
//            rowsInLine.add(rowInLine);
//            inlineKeyboardMarkup.setKeyboard(rowsInLine);
//            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//        }
//        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
//        markup.setResizeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        markup.setKeyboard(keyboardRows);
//        sendMessage.setReplyMarkup(markup);
//        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//    }
//
    private void sendRegions(SendMessage sendMessage, List<Region> all) {
        System.out.println(all.size());
        sendMessage.setText("Siz o'zingizni viloyatingizni tanlab oling!!");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (Region compulsoryChannel : all) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(compulsoryChannel.getRegionShowName());
            inlineKeyboardButton.setCallbackData(compulsoryChannel.getId().toString());
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
//
//
//    private void adminPageMethod(Update update, User user, SendMessage sendMessage) {
//        String stepOn = update.getMessage().getText();
//
//        sendMessage.setText("Siz Adminstrator pagedasiz");
//        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
//        markup.setResizeKeyboard(true);
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        KeyboardRow firstRow = new KeyboardRow();
//        firstRow.add(new KeyboardButton(Constants.adminStep2));
//        firstRow.add(new KeyboardButton(Constants.adminStep3));
//        keyboardRows.add(firstRow);
//        KeyboardRow secondRow = new KeyboardRow();
//        secondRow.add(new KeyboardButton(Constants.adminStep4));
//        secondRow.add(new KeyboardButton(Constants.adminStep6));
//        keyboardRows.add(secondRow);
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRow.add(new KeyboardButton(Constants.adminStep5));
//        keyboardRows.add(keyboardRow);
//        markup.setKeyboard(keyboardRows);
//        sendMessage.setReplyMarkup(markup);
//        if (stepOn.equals(Constants.adminStep1)) {
//        } else if (stepOn.equals(Constants.adminStep2)) {
//            sendMessage.setText(Constants.adminStep2);
//        } else if (stepOn.equals(Constants.adminStep3)) {
//            sendMessage.setText(Constants.adminStep3);
//        } else if (stepOn.equals(Constants.adminStep4)) {
//            sendMessage.setText(Constants.adminStep4);
//
//        } else if (stepOn.equals(Constants.adminStep5)) {
//            sendMessage.setText(Constants.adminStep5);
//        } else if (stepOn.equals(Constants.adminStep6)) {
//            sendMessage.setText(Constants.adminStep6);
//        } else {
//            sendMessage.setText("Wrong step found");
//        }
//    }
//
//
////    @Scheduled(cron = "30 0 * * *")
//    private void timeSender() {
//        String url = "https://islomapi.uz/api/present/day?region=";
//        List<Region> regionAll = regionRepo.findAll();
//        List<TimeResponseDto> res = new ArrayList<>();
//        TimeResponseDto item;
//        for (Region region : regionAll) {
//            item = new RestTemplate().getForObject(url + region.getName(), TimeResponseDto.class);
//            res.add(item);
//        }
//
//
//    }
//
//
//    private List<TimeResponseDto> sortTimeResponseDtoByTime(List<TimeResponseDto> res) {
//
//        return null;
//    }
//
//
//    private LocalTime makeLocalTime(String str) {
//        String[] split = str.split(":");
//        return LocalTime.of(Integer.parseInt(split[1]), Integer.parseInt(split[0]));
//    }

}
