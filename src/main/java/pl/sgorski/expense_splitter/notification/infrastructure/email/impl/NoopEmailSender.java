package pl.sgorski.expense_splitter.notification.infrastructure.email.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.infrastructure.email.EmailSender;

@Profile("test")
@Component
@Slf4j
public final class NoopEmailSender implements EmailSender {
  @Override
  public void send(String to, String subject, String content) {
    log.info("[NOOP EMAIL SENDER] To: {}, Subject: {}, Content: {}", to, subject, content);
  }
}
