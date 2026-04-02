package pl.sgorski.expense_splitter.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

  private static final int SALT_LENGTH = 16; // bytes
  private static final int HASH_LENGTH = 32; // bytes
  private static final int PARALLELISM = 1; // threads
  private static final int MEMORY = 65536; // KB = 64 MB
  private static final int ITERATIONS = 4; // time cost

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, ITERATIONS);
  }
}
