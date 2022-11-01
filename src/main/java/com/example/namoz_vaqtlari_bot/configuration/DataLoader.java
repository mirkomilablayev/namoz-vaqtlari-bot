package com.example.namoz_vaqtlari_bot.configuration;


import com.example.namoz_vaqtlari_bot.controller.MyBotService;
import com.example.namoz_vaqtlari_bot.entity.CompulsoryChannel;
import com.example.namoz_vaqtlari_bot.entity.ConChannel;
import com.example.namoz_vaqtlari_bot.entity.Region;
import com.example.namoz_vaqtlari_bot.entity.UserRole;
import com.example.namoz_vaqtlari_bot.repository.CompulsoryChannelRepo;
import com.example.namoz_vaqtlari_bot.repository.ConChannelRepo;
import com.example.namoz_vaqtlari_bot.repository.RegionRepo;
import com.example.namoz_vaqtlari_bot.repository.RoleRepository;
import com.example.namoz_vaqtlari_bot.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@RequiredArgsConstructor
@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final MyBotService myBotService;
    private final RoleRepository roleRepository;
    private final CompulsoryChannelRepo compulsoryChannelRepo;

    private final ConChannelRepo conChannelRepo;
    private final RegionRepo regionRepo;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;


    @Value("${bot.token}")
    private String botToken;

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this.myBotService);



        regionRepo.save(new Region(1L, "Toshkent" , "Toshkent Vil"));
        regionRepo.save(new Region(2L, "Andijon" , "Andijon Vil"));
        regionRepo.save(new Region(3L, "Namangan" , "Namangan Vil"));
        regionRepo.save(new Region(4L, "Jizzax" , "Jizzax vil"));
        regionRepo.save(new Region(5L, "Samarqand" , "Samarqand vil"));
        regionRepo.save(new Region(6L, "Navoiy" , "Navoiy vil"));
        regionRepo.save(new Region(7L, "Buxoro" , "Buxoro vil"));
        regionRepo.save(new Region(8L, "Nukus" , "Qoraqalpogoston res"));
        regionRepo.save(new Region(9L, "Urganch" , "Xorazm vil"));
        regionRepo.save(new Region(10L, "Qarshi" , "Qashqadaryo"));
        regionRepo.save(new Region(11L, "Termiz" , "Surxandaryo Vil"));
        regionRepo.save(new Region(12L, "Guliston" , "Sirdaryo vil"));
        regionRepo.save(new Region(13L, "Urganch" , "Xorazm"));

        roleRepository.save(new UserRole(Constants.USER));
        roleRepository.save(new UserRole(Constants.ADMIN));
        System.out.println(botToken);
        String url = "https://api.telegram.org/bot"+botToken+"/getChat?chat_id=@djhdjfhdhjfdhfdjhfj";
        try{
            CompulsoryChannel forObject = new RestTemplate().getForObject(url, CompulsoryChannel.class);
            assert forObject != null;

            CompulsoryChannel compulsoryChannel = new CompulsoryChannel();
            ConChannel conChannel = new ConChannel();
            conChannel.setType(forObject.getResult().getType());
            conChannel.setTitle(forObject.getResult().getTitle());
            conChannel.setId(forObject.getResult().getId());
            conChannel.setUsername(forObject.getResult().getUsername());
            conChannel.setInvite_link(forObject.getResult().getInvite_link());
            ConChannel save1 = conChannelRepo.save(conChannel);
            compulsoryChannel.setResult(save1);
            CompulsoryChannel save = compulsoryChannelRepo.save(compulsoryChannel);
            log.info("NEW COMPULSORY CHANNEL SAVED : "+save);
        }catch (Exception e){
           log.info(e.getMessage());
        }



    }




}
