package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.CodeGenerationException;
import pl.sgorski.expense_splitter.utils.QrCodeUtils;

@Service
public final class TotpService {

  private static final String ISSUER = "ExpenseSplitter";
  private static final int QR_CODE_SIZE = 200;
  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

  public String generateSecret() {
    return googleAuthenticator.createCredentials().getKey();
  }

  public boolean verify(String secret, int code) {
    return googleAuthenticator.authorize(secret, code);
  }

  public byte[] buildOtpAuthUrl(String email, String secret) {
    var authKey = new GoogleAuthenticatorKey.Builder(secret).build();
    var url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUER, email, authKey);
    return QrCodeUtils.generate(url, QR_CODE_SIZE)
        .orElseThrow(() -> new CodeGenerationException("Failed to generate QR code for 2FA"));
  }
}
