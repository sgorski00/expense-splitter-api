package pl.sgorski.expense_splitter.security.websocket;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import pl.sgorski.expense_splitter.security.jwt.JwtService;
import pl.sgorski.expense_splitter.utils.AuthorizationTokenUtils;

@Component
@RequiredArgsConstructor
public final class JwtHandshakeInterceptor implements HandshakeInterceptor {

  public static final String USER_ID_ATTRIBUTE = "userId";
  private final JwtService jwtService;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes)
      throws Exception {
    var header = request.getHeaders().getFirst(AuthorizationTokenUtils.AUTHORIZATION_HEADER);
    var token = AuthorizationTokenUtils.getTokenFromHeader(header);
    if (token == null || jwtService.isTokenInvalid(token)) {
      return false;
    }
    var userId = jwtService.getUserId(token);
    attributes.put(USER_ID_ATTRIBUTE, userId);
    return true;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      @Nullable Exception exception) {}
}
