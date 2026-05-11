package pl.sgorski.expense_splitter.features.auth.two_fa.service;

public interface TotpService {
  boolean verify(String secret, int code);

  String generateSecret();

  byte[] buildOtpAuthUrl(String email, String secret);
}
