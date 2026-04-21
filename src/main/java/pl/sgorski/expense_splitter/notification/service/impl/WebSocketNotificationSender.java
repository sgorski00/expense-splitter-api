package pl.sgorski.expense_splitter.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.infrastructure.websocket.WebSocketSender;
import pl.sgorski.expense_splitter.notification.service.NotificationSender;

@Component
@RequiredArgsConstructor
public final class WebSocketNotificationSender implements NotificationSender {

    private final WebSocketSender sender;

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBSOCKET;
    }

    @Override
    public void send(Notification notification) {
        sender.send(notification.getUser().getId(), notification.getTitle() + ": " + notification.getBody());
    }
}
