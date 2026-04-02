package pl.sgorski.expense_splitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.sgorski.expense_splitter.features.auth.refresh_token.config.RefreshTokenProperties;
import pl.sgorski.expense_splitter.security.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, RefreshTokenProperties.class})
@EnableScheduling
public class ExpenseSplitterApplication {

  static void main(String[] args) {
    SpringApplication.run(ExpenseSplitterApplication.class, args);
  }
}
