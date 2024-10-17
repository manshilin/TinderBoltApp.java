package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "my_telegramtinder_bot"; 
    public static final String TELEGRAM_BOT_TOKEN = "8138281864:AAE9qULm0sisyF2Ojbs-3PdNFgA22ollFN8"; 
    public static final String OPEN_AI_TOKEN = "gpt:"; 
    public DialogMode mode = DialogMode.MAIN;
    private ArrayList<String> chat;

    public ChatGPTService gptService = new ChatGPTService(OPEN_AI_TOKEN);

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        if (message.equals("/start")) {
            mode = DialogMode.MAIN;
            showMainMenu(
                "Main menu", "/start",
                "генерація Tinder-профілю 😎", "/profile",
                
                 "повідомлення для знайомства 🥰", "/opener",
                "листування від вашого імені 😈", "/message",
                "листування із зірками 🔥", "/date",
                "поставити запитання чату GPT 🧠", "/gpt"

            );
            String menu = loadMessage("main");
            sendTextMessage(menu);
            sendPhotoMessage("main");

            return;
        }
        
        if (message.equals("/gpt")) {
            mode = DialogMode.GPT;

            String gptMessage = loadMessage("gpt");
            sendTextMessage(gptMessage);
            sendPhotoMessage("gpt");

            return;
        }
        if (mode == DialogMode.GPT) {
            String promt = loadPrompt("gpt");
            Message msg = sendTextMessage("Wait");
            String answer = gptService.sendMessage(promt, message);
            updateTextMessage(msg, answer);
            return;

        }
        if (message.equals("/date")) {
            mode = DialogMode.DATE;

            String dateMessage = loadMessage("date");
            sendPhotoMessage("date");

            sendTextButtonsMessage(dateMessage, 
            "Ariana Grande","date_grande",
            "Margot Robbie ","date_robbie",
            "Zendaya ","date_zendaya",
            "Ryan Gosling ","date_gosling",
            "Tom Hardy ","date_hardy");

            return;
        }

        if (mode == DialogMode.DATE) {
            String query = getCallbackQueryButtonKey();

            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                String promt = loadPrompt(query);
                gptService.setPrompt(promt);
                
                return;
            }
            Message msg = sendTextMessage("Wait");
            String answer = gptService.addMessage(message);
            updateTextMessage(msg, answer);
            return;
        }
        if (message.equals("/message")) {
            mode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            String gptMessageHelper = loadMessage("message");
            sendTextMessage(gptMessageHelper);

            sendTextButtonsMessage(gptMessageHelper,
             "message next","message_next",
             "invite on a date", "message_date");
            
            chat = new ArrayList<>();

            return;
        }

        if(mode == DialogMode.MESSAGE) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                String promt = loadPrompt(query);
                String history = String.join("\n\n", chat);

                Message msg = sendTextMessage("Wait");

                String answer = gptService.sendMessage(promt, history);
                updateTextMessage(msg, answer);
                sendTextMessage(answer);
            }
            chat.add(message);
            return;
        }
    }
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
