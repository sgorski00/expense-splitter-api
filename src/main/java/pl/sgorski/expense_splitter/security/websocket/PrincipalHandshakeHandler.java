package pl.sgorski.expense_splitter.security.websocket;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public final class PrincipalHandshakeHandler extends DefaultHandshakeHandler {

  @Override
  protected Principal determineUser(
      ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
    var userId = (String) attributes.get(JwtHandshakeInterceptor.USER_ID_ATTRIBUTE);
    return () -> userId;
  }
}
