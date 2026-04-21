package pl.sgorski.expense_splitter.security.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.security.jwt.JwtService;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public final class StompAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log.trace("Received WebSocket message");
    var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) {
      log.trace("Accessor is null, skipping authentication");
      return message;
    }

    log.trace("Message command: {}", accessor.getCommand());
    if (accessor.getCommand() == StompCommand.CONNECT) {
      var authHeader = accessor.getFirstNativeHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
      var token = AuthorizationTokenUtils.getTokenFromHeader(authHeader);
      if (token == null || jwtService.isTokenInvalid(token)) {
        log.warn("Invalid access token provided for WebSocket connection");
        throw new IllegalArgumentException("Invalid token");
      }
      log.trace("Valid access token received");
      var userId = jwtService.getUserId(token);
      accessor.setUser(() -> userId);
      log.info("User with id {} connected to WebSocket", userId);
    }
    return message;
  }
}
