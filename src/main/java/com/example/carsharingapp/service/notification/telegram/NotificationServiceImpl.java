package com.example.carsharingapp.service.notification.telegram;

import com.example.carsharingapp.config.TelegramConfig;
import com.example.carsharingapp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final TelegramBot telegramBot;
    private final TelegramConfig telegramConfig;

    @Override
    public void sendMessageAdmin(String message) {
        long adminChatId = telegramConfig.getChatId();
        try {
            telegramBot.sendMessage(adminChatId, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
