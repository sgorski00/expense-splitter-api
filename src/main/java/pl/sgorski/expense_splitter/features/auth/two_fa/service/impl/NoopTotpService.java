package pl.sgorski.expense_splitter.features.auth.two_fa.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.features.auth.two_fa.config.TestTotpProperties;
import pl.sgorski.expense_splitter.features.auth.two_fa.service.TotpService;

@Service
@Profile("test")
@RequiredArgsConstructor
public final class NoopTotpService implements TotpService {

  private final TestTotpProperties testTotpProperties;

  @Override
  public boolean verify(String secret, int code) {
    return code == testTotpProperties.code();
  }

  @Override
  public String generateSecret() {
    return testTotpProperties.secret();
  }

  @Override
  public byte[] buildOtpAuthUrl(String email, String secret) {
    return new byte[0];
  }
}
