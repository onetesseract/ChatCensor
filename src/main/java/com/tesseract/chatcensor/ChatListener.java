package com.tesseract.chatcensor;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;

public class ChatListener {
    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {

        final String[] message = {event.getMessage()};
        Chatcensor.banned.forEach(word -> {
            if (message[0].toLowerCase().contains(word.toLowerCase())) {
                if (Chatcensor.caseSensitive) {
                    message[0] = message[0].replaceAll("(?i)"+word.toLowerCase(), Chatcensor.censor.repeat(word.length()));
                } else {
                    message[0] = message[0].replace(word, Chatcensor.censor.repeat(word.length()));
                }
            }
        });
        event.setResult(PlayerChatEvent.ChatResult.message(message[0]));

    }
}
