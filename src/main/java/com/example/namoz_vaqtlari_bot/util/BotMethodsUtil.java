package com.example.namoz_vaqtlari_bot.util;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BotMethodsUtil {



    public String getChatId(Update update){
         if (update.hasChannelPost()){
            return update.getChannelPost().getChatId().toString();
        }else if(update.hasCallbackQuery()){
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }else if(update.hasChatMember()){
            return update.getChatMember().getChat().getId().toString();
        }else if (update.hasEditedMessage()){
            return update.getEditedMessage().getChatId().toString();
        }else {
             return update.getMessage().getChatId().toString();
         }
    }

}
