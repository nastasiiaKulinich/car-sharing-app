package com.example.carsharingapp.service.notification.telegram;

import com.example.carsharingapp.config.TelegramConfig;
import com.example.carsharingapp.exception.NotificationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramConfig telegramConfig;

    public TelegramBot(TelegramConfig telegramConfig) {
        super(telegramConfig.getBotToken());
        this.telegramConfig = telegramConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommendReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getBotName();
    }

    private void startCommendReceived(long chatId, String firstName) {
        String response = """
            Hi, %s! I'm your notification bot
            I'll provide notifications about:
            - New rentals
            - Overdue rentals
            - Successful payments
                """.formatted(firstName);

        sendMessage(chatId, response);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new NotificationException(
                    "Failed to send message to chat " + chatId + ": " + e.getMessage());
        }
    }
}
