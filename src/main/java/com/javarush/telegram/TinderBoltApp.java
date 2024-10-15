package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "my_telegramtinder_bot"; 
    public static final String TELEGRAM_BOT_TOKEN = "8138281864:AAE9qULm0sisyF2Ojbs-3PdNFgA22ollFN8"; 
    public static final String OPEN_AI_TOKEN = "chat-gpt-token"; 
    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        if (message.equals("/start")) {
            sendTextMessage("*HELLO!*");
            sendPhotoMessage("main");

            return;
        }
        
        sendTextMessage("_" + message + "_");
        sendTextButtonsMessage("Button message",
        "START", "start",
        "STOP", "stop"); 
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
