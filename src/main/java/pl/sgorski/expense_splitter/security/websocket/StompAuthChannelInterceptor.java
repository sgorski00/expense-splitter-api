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
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    log.trace("Received WebSocket message with accessor: {}", accessor);
    if (accessor == null) {
      return message;
    }

    log.trace("Received WebSocket message with command: {}", accessor.getCommand());
    if (accessor.getCommand() == StompCommand.CONNECT) {
      var authHeader = accessor.getFirstNativeHeader(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
      var token = AuthorizationTokenUtils.getTokenFromHeader(authHeader);
      log.trace("Received WebSocket with token: {}", token);
      if (token == null || jwtService.isTokenInvalid(token)) {
        log.warn("Token {} is invalid", token);
        throw new IllegalArgumentException("Invalid token");
      }
      var userId = jwtService.getUserId(token);
      accessor.setUser(() -> userId);
      log.info("User with id {} connected to WebSocket", userId);
    }
    return message;
  }
}
