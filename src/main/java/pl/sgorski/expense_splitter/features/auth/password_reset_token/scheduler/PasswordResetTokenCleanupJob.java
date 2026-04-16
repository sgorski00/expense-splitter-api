package pl.sgorski.expense_splitter.features.auth.password_reset_token.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.auth.password_reset_token.service.PasswordResetTokenService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetTokenCleanupJob {

  private final PasswordResetTokenService passwordResetTokenService;

  @Async
  @Scheduled(cron = "0 0 * * * *")
  public void cleanUpInvalidTokens() {
    log.info("Cleaning up expired and revoked password reset tokens...");
    passwordResetTokenService.deleteInvalidTokens();
  }
}
