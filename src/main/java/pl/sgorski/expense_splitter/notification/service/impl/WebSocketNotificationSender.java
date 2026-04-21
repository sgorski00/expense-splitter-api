package pl.sgorski.expense_splitter.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.domain.Notification;
import pl.sgorski.expense_splitter.notification.domain.NotificationChannel;
import pl.sgorski.expense_splitter.notification.infrastructure.websocket.WebSocketSender;
import pl.sgorski.expense_splitter.notification.mapper.NotificationMapper;
import pl.sgorski.expense_splitter.notification.service.NotificationSender;

@Component
@RequiredArgsConstructor
public final class WebSocketNotificationSender implements NotificationSender {

    private final WebSocketSender sender;
    private final NotificationMapper mapper;

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBSOCKET;
    }

    @Override
    public void send(Notification notification) {
        var dto = mapper.toWsDto(notification);
        sender.send(notification.getUser().getId(), dto);
    }
}
