package pl.sgorski.expense_splitter.notification.infrastructure.email;

public interface EmailSender {
  void send(String to, String subject, String content);
}
