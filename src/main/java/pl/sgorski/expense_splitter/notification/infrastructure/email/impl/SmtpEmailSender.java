package pl.sgorski.expense_splitter.notification.infrastructure.email.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.notification.config.MailProperties;
import pl.sgorski.expense_splitter.notification.infrastructure.email.EmailSender;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

  private final MailProperties mailProperties;
  private final JavaMailSender javaMailSender;

  @Override
  public void send(String to, String subject, String content) {
    var message = new SimpleMailMessage();
    message.setFrom(mailProperties.username());
    message.setTo(to);
    message.setSubject(subject);
    message.setText(content);
    javaMailSender.send(message);
  }
}
