package pl.sgorski.expense_splitter.security.websocket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import pl.sgorski.expense_splitter.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
public class StompAuthChannelInterceptorTest {

  @Mock private JwtService jwtService;

  @InjectMocks private StompAuthChannelInterceptor interceptor;

  @Mock private MessageChannel channel;

  @Test
  void preSend_shouldReturnMessage_whenAccessorIsNotStompHeaderAccessor() {
    try (var mockHeaderAccessor = mockStatic(MessageHeaderAccessor.class)) {
      mockHeaderAccessor
          .when(
              () ->
                  MessageHeaderAccessor.getAccessor(
                      any(Message.class), eq(StompHeaderAccessor.class)))
          .thenReturn(null);

      var message =
          MessageBuilder.createMessage(
              new byte[0], new MessageHeaderAccessor().getMessageHeaders());

      var result = interceptor.preSend(message, channel);

      assertEquals(message, result);
      verifyNoInteractions(jwtService);
    }
  }

  @Test
  void preSend_shouldReturnMessage_whenCommandIsNotConnect() {
    var accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    var result = interceptor.preSend(message, channel);

    assertEquals(message, result);
    verifyNoInteractions(jwtService);
  }

  @Test
  void preSend_shouldThrowException_whenTokenIsNull() {
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", null);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(message, channel));
    verifyNoInteractions(jwtService);
  }

  @Test
  void preSend_shouldThrowException_whenTokenIsInvalid() {
    var token = "invalidToken";
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    accessor.setLeaveMutable(true);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(message, channel));
    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verifyNoMoreInteractions(jwtService);
  }

  @Test
  void preSend_shouldSetUserWithUserId_whenTokenIsValid() {
    var token = "validToken";
    var userId = UUID.randomUUID().toString();
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    accessor.setLeaveMutable(true);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    when(jwtService.isTokenInvalid(eq(token))).thenReturn(false);
    when(jwtService.getUserId(eq(token))).thenReturn(userId);

    var result = interceptor.preSend(message, channel);

    verify(jwtService, times(1)).isTokenInvalid(eq(token));
    verify(jwtService, times(1)).getUserId(eq(token));
    verifyNoMoreInteractions(jwtService);
    var resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
    assertNotNull(resultAccessor);
    assertNotNull(resultAccessor.getUser());
    assertEquals(userId, resultAccessor.getUser().getName());
  }
}
