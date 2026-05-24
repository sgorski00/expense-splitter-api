package pl.sgorski.expense_splitter.security.sentry;

import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sentry.enabled", havingValue = "true")
public final class SentryReporter {

  public void capture(Exception exception) {
    Sentry.captureException(exception);
  }

  public SentryId getLastEventId() {
    return Sentry.getLastEventId();
  }
}
