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
import pl.sgorski.expense_splitter.security.jwt.payload.AccessTokenPayload;
import pl.sgorski.expense_splitter.security.jwt.service.AccessTokenService;
import pl.sgorski.expense_splitter.security.jwt.service.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class StompAuthChannelInterceptorTest {

  @Mock private JwtProvider jwtProvider;
  @Mock private AccessTokenService accessTokenService;

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
      verifyNoInteractions(jwtProvider);
    }
  }

  @Test
  void preSend_shouldReturnMessage_whenCommandIsNotConnect() {
    var accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    var result = interceptor.preSend(message, channel);

    assertEquals(message, result);
    verifyNoInteractions(jwtProvider);
  }

  @Test
  void preSend_shouldThrowException_whenTokenIsNull() {
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", null);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(message, channel));
    verifyNoInteractions(jwtProvider);
  }

  @Test
  void preSend_shouldThrowException_whenTokenIsInvalid() {
    var token = "invalidToken";
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    accessor.setLeaveMutable(true);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    when(jwtProvider.isInvalid(eq(token))).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(message, channel));
    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verifyNoMoreInteractions(jwtProvider);
  }

  @Test
  void preSend_shouldSetUserWithUserId_whenTokenIsValid() {
    var token = "validToken";
    var userId = UUID.randomUUID();
    var payload = new AccessTokenPayload(userId, "user@example.com", false, false);
    var accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    accessor.setLeaveMutable(true);
    var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    when(jwtProvider.isInvalid(eq(token))).thenReturn(false);
    when(accessTokenService.parse(eq(token))).thenReturn(payload);

    var result = interceptor.preSend(message, channel);

    verify(jwtProvider, times(1)).isInvalid(eq(token));
    verify(accessTokenService, times(1)).parse(eq(token));
    verifyNoMoreInteractions(jwtProvider);
    var resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
    assertNotNull(resultAccessor);
    assertNotNull(resultAccessor.getUser());
    assertEquals(userId.toString(), resultAccessor.getUser().getName());
  }
}
