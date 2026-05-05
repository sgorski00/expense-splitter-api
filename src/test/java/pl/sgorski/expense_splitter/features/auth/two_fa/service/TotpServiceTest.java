package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import static org.junit.jupiter.api.Assertions.*;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TotpServiceTest {

  private TotpService totpService;
  private GoogleAuthenticator googleAuthenticator;

  @BeforeEach
  void setUp() {
    totpService = new TotpService();
    googleAuthenticator = new GoogleAuthenticator();
  }

  @Test
  void generateSecret_shouldReturnValidSecret() {
    var secret = totpService.generateSecret();

    assertNotNull(secret);
    assertFalse(secret.isEmpty());
    assertTrue(secret.length() >= 16);
  }

  @Test
  void verify_shouldReturnTrue_forValidCode() {
    var secret = googleAuthenticator.createCredentials().getKey();
    var code = googleAuthenticator.getTotpPassword(secret);

    var result = totpService.verify(secret, code);

    assertTrue(result);
  }

  @Test
  void verify_shouldReturnFalse_forInvalidCode() {
    var secret = googleAuthenticator.createCredentials().getKey();
    var invalidCode = 123456;

    var result = totpService.verify(secret, invalidCode);

    assertFalse(result);
  }

  @Test
  void buildOtpAuthUrl_shouldReturnByteArray_whenSuccessful() {
    var email = "user@example.com";
    var secret = googleAuthenticator.createCredentials().getKey();

    var result = totpService.buildOtpAuthUrl(email, secret);

    assertNotNull(result);
    assertTrue(result.length > 0);
  }
}
