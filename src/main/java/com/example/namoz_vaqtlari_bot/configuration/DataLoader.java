package com.example.namoz_vaqtlari_bot.configuration;


import com.example.namoz_vaqtlari_bot.controller.MyBotService;
import com.example.namoz_vaqtlari_bot.entity.CompulsoryChannel;
import com.example.namoz_vaqtlari_bot.entity.UserRole;
import com.example.namoz_vaqtlari_bot.repository.CompulsoryChannelRepo;
import com.example.namoz_vaqtlari_bot.repository.RoleRepository;
import com.example.namoz_vaqtlari_bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final MyBotService myBotService;
    private final RoleRepository roleRepository;
    private final CompulsoryChannelRepo compulsoryChannelRepo;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this.myBotService);

        roleRepository.save(new UserRole(Constants.USER));
        roleRepository.save(new UserRole(Constants.ADMIN));

        compulsoryChannelRepo.save(new CompulsoryChannel("Mirkomil Dev Blog | Rasmiy Kanal","mirkomil_dev"));


    }




}
