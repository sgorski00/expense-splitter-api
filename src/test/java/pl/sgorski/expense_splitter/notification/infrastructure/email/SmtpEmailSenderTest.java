package pl.sgorski.expense_splitter.notification.infrastructure.email;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.sgorski.expense_splitter.notification.config.MailProperties;
import pl.sgorski.expense_splitter.notification.infrastructure.email.impl.SmtpEmailSender;

@ExtendWith(MockitoExtension.class)
public class SmtpEmailSenderTest {

  @Mock private MailProperties mailProperties;
  @Mock private JavaMailSender javaMailSender;
  @InjectMocks private SmtpEmailSender smtpEmailSender;

  @Test
  void send_shouldSendEmail_whenPropertiesAreValid() {
    var to = "recipient@example.com";
    var subject = "Test Subject";
    var content = "Test Content";
    var from = "sender@example.com";
    when(mailProperties.username()).thenReturn(from);

    smtpEmailSender.send(to, subject, content);

    verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    verify(mailProperties, times(1)).username();
  }
}
