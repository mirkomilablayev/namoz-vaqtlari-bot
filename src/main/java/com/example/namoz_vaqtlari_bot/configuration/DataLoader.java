package com.example.namoz_vaqtlari_bot.configuration;


import com.example.namoz_vaqtlari_bot.entity.Region;
import com.example.namoz_vaqtlari_bot.repository.RegionRepository;
import com.example.namoz_vaqtlari_bot.service.MyBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@RequiredArgsConstructor
@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final MyBotService myBotService;
    private final RegionRepository regionRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;



    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this.myBotService);

        if (ddl.equals("create") || ddl.equals("create-drop")){
            System.out.println("s");


            regionRepository.save(new Region( "Toshkent" , "Toshkent viloyati"));
            regionRepository.save(new Region( "Andijon" , "Andijon viloyati"));
            regionRepository.save(new Region( "Namangan" , "Namangan viloyati"));
            regionRepository.save(new Region( "Jizzax" , "Jizzax viloyati"));
            regionRepository.save(new Region( "Samarqand" , "Samarqand viloyati"));
            regionRepository.save(new Region( "Navoiy" , "Navoiy viloyati"));
            regionRepository.save(new Region( "Buxoro" , "Buxoro viloyati"));
            regionRepository.save(new Region( "Nukus" , "Qoraqalpogoston respublikasi"));
            regionRepository.save(new Region( "Urganch" , "Xorazm viloyati"));
            regionRepository.save(new Region( "Qarshi" , "Qashqadaryo viloyati"));
            regionRepository.save(new Region( "Termiz" , "Surxandaryo viloyati"));
            regionRepository.save(new Region( "Guliston" , "Sirdaryo viloyati"));
            regionRepository.save(new Region( "Urganch" , "Xorazm viloyati"));

            }
    }




}
