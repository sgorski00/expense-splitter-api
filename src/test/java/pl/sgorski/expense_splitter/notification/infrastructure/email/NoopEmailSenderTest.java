package pl.sgorski.expense_splitter.notification.infrastructure.email;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.notification.infrastructure.email.impl.NoopEmailSender;

@ExtendWith(MockitoExtension.class)
public class NoopEmailSenderTest {

  @InjectMocks private NoopEmailSender noopEmailSender;

  @Test
  void send_shouldNotThrowException_whenSendingEmail() {
    var to = "user@example.com";
    var subject = "Test Subject";
    var content = "Test Content";

    assertDoesNotThrow(() -> noopEmailSender.send(to, subject, content));
  }
}
