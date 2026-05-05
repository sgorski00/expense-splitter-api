package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.SecretEncryptionException;

public class SecretEncryptorTest {

  private SecretEncryptor secretEncryptor;

  @BeforeEach
  void setUp() throws Exception {
    var keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256);
    var secretKey = keyGen.generateKey();
    secretEncryptor = new SecretEncryptor(secretKey);
  }

  @Test
  void encryptAndDecrypt_shouldReturnOriginalString() {
    var originalText = "SuperSecretTOTPKey123!";

    var encrypted = secretEncryptor.encrypt(originalText);
    assertNotNull(encrypted);
    assertNotEquals(originalText, encrypted);

    var decrypted = secretEncryptor.decrypt(encrypted);
    assertEquals(originalText, decrypted);
  }

  @Test
  void decrypt_shouldThrowException_whenInputIsInvalidBase64() {
    var invalidBase64 = "not-a-base64-string!";

    assertThrows(SecretEncryptionException.class, () -> secretEncryptor.decrypt(invalidBase64));
  }

  @Test
  void decrypt_shouldThrowException_whenInputIsTooShort() {
    var tooShort = Base64.getEncoder().encodeToString("short".getBytes(StandardCharsets.UTF_8));

    assertThrows(SecretEncryptionException.class, () -> secretEncryptor.decrypt(tooShort));
  }

  @Test
  void decrypt_shouldThrowException_whenDataIsTampered() {
    var originalText = "SensitiveData";
    var encrypted = secretEncryptor.encrypt(originalText);
    var decoded = Base64.getDecoder().decode(encrypted);

    decoded[decoded.length - 1] ^= 1;
    var tampered = Base64.getEncoder().encodeToString(decoded);

    assertThrows(SecretEncryptionException.class, () -> secretEncryptor.decrypt(tampered));
  }
}
