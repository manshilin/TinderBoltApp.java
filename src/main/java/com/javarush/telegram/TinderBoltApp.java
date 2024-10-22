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
    public static final String OPEN_AI_TOKEN = "gpt:T0iTZNHd48ZlWS64y3EfJFkblB3TMr4vkS5kVmutmQNPEEps"; 
    public DialogMode mode = DialogMode.MAIN;
    private ArrayList<String> chat;
    private UserInfo myInfo;
    private UserInfo personInfo;
    private int questionNumber;

    public ChatGPTService gptService = new ChatGPTService(OPEN_AI_TOKEN);

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        switch (message) {
            case "/start" -> {
                mode = DialogMode.MAIN;

                showMainMenu(
                    
                "Main menu", "/start",
                "генерація Tinder-профілю 😎", "/profile",
                 "повідомлення для знайомства 🥰", "/opener",
                "листування від вашого імені 😈", "/message",
                "листування із зірками 🔥", "/date",
                "поставити запитання чату GPT 🧠", "/gpt"
                );

                sendPhotoMessage( "main");
                String menu = loadMessage("main");
                sendTextMessage(menu);
                return;
            }
            
            case "/gpt" -> {
                mode = DialogMode.GPT;

                sendPhotoMessage( "gpt");
                String gptMessage = loadMessage("gpt");
                sendTextMessage(gptMessage);
                return;
            }

            case "/date" -> {
                mode = DialogMode.DATE;

                sendPhotoMessage("date");
                String dateMessage = loadMessage("date");
                sendTextButtonsMessage(dateMessage, 
                "Ariana Grande","date_grande",
                "Margot Robbie ","date_robbie",
                "Zendaya ","date_zendaya",
                "Ryan Gosling ","date_gosling",
                "Tom Hardy ","date_hardy"); 
                return;
            }

            case "/message" -> {
                mode = DialogMode.MESSAGE;
                sendPhotoMessage("message");
                String gptMessageHelper = loadMessage("message");
                sendTextButtonsMessage(gptMessageHelper,
                "Next", "message_next",
                "invite to a date", "message_date");

                chat = new ArrayList<>();
                return;
            }
            case "/profile" -> {
                mode = DialogMode.PROFILE;
                sendPhotoMessage("profile");
                String profileMessage = loadMessage("profile");
                sendTextMessage(profileMessage);

                myInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Input name?");
                return;
            }
            case "/opener" -> {
                mode = DialogMode.OPENER;
                sendPhotoMessage("opener");
                String profileMessage = loadMessage("opener");
                sendTextMessage(profileMessage);

                personInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Input name?");
                return;
            }
        }

        switch (mode) {
            case MAIN -> {
                // Дії для MAIN (поки що порожньо)
            }
            
            case GPT -> {
                // Дії для GPT
                String prompt = loadPrompt("gpt");
                Message msg = sendTextMessage("ChatGPT print...");
                String answer = gptService.sendMessage(prompt, message);
                updateTextMessage(msg, answer);
            }
            case DATE -> {
                // Дії для DATE
                String query = getCallbackQueryButtonKey();
                if (query.startsWith("date_")) {
                    sendPhotoMessage(query);
                    String prompt = loadPrompt(query);
                    gptService.setPrompt(prompt);
                }
            }
            case MESSAGE -> {
                // Дії для MESSAGE
                String query = getCallbackQueryButtonKey();

                if (query.startsWith("message_")) {
                    String prompt = loadPrompt(query);
                    String history = String.join("\n", chat);

                    Message msg = sendTextButtonsMessage("ChatGPT print ...");

                    String answer = gptService.sendMessage(prompt, history);
                    updateTextMessage(msg, answer);
                }
                chat.add(message);
            }
            case PROFILE -> {
                if (questionNumber <= 6) {
                    askQuestion(message, myInfo, "profile");
                }
            } 

            case OPENER -> {
                if (questionNumber <= 6) {
                    askQuestion(message, personInfo,  "opener");
                }
            }
        }
    }        
    private void askQuestion(String message, UserInfo user, String profileName){
        switch (questionNumber) {
            case 1 -> {
                user.name = message;
                questionNumber = 2;
                sendTextMessage("Input age");
                return;

            }
            case 2 -> {
                user.age = message;
                questionNumber = 3;
                sendTextMessage("Input city?");

                return;
            }

            case 3 -> {
                user.city = message;
                questionNumber = 4;
                sendTextMessage("Input profession?");

                return;
            }
            case 4 -> {
                user.occupation = message;
                questionNumber = 5;
                sendTextMessage("Input hobby?");

                return;
            }

            case 5 -> {
                user.hobby = message;
                questionNumber = 6;
                sendTextMessage("Input goals?");
                
                return;
            }
            case 6 -> {
                user.goals = message;

                String promt = loadPrompt( "profileName");
                Message msg = sendTextMessage("ChatGPT print...");
                String answer = gptService.sendMessage(promt, user.toString());
                updateTextMessage(msg, answer);
                
                return;
            }
        }
    }
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
