package pl.sgorski.expense_splitter.features.auth.two_fa.service;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.sgorski.expense_splitter.exceptions.authentication.two_fa.SecretEncryptionException;

@Service
@Slf4j
public final class SecretEncryptor {

  private static final Charset CHARSET = StandardCharsets.UTF_8;
  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int IV_LENGTH = 12;
  private static final int TAG_LENGTH = 128;

  private final SecretKey key;

  public SecretEncryptor(@Qualifier("twoFaEncryptionKey") SecretKey key) {
    this.key = key;
  }

  public String encrypt(String plain) {
    try {
      var iv = new byte[IV_LENGTH];
      new SecureRandom().nextBytes(iv);

      var cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

      var encrypted = cipher.doFinal(plain.getBytes(CHARSET));

      var combined =
          ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array();

      return Base64.getEncoder().encodeToString(combined);

    } catch (Exception e) {
      log.error("Encrypt failed", e);
      throw new SecretEncryptionException("Failed to encrypt secret", e);
    }
  }

  public String decrypt(String encrypted) {
    try {
      var decoded = Base64.getDecoder().decode(encrypted);

      var buffer = ByteBuffer.wrap(decoded);

      var iv = new byte[IV_LENGTH];
      buffer.get(iv);

      var ciphertext = new byte[buffer.remaining()];
      buffer.get(ciphertext);

      var cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

      var decrypted = cipher.doFinal(ciphertext);

      return new String(decrypted, CHARSET);

    } catch (Exception e) {
      log.error("Decrypt failed", e);
      throw new SecretEncryptionException("Failed to decrypt secret", e);
    }
  }
}
