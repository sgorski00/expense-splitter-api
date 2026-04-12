package pl.sgorski.expense_splitter;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.sgorski.expense_splitter.features.auth.refresh_token.config.RefreshTokenProperties;
import pl.sgorski.expense_splitter.notification.config.MailProperties;
import pl.sgorski.expense_splitter.security.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({
  JwtProperties.class,
  RefreshTokenProperties.class,
  MailProperties.class
})
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ExpenseSplitterApplication {

  static void main(String[] args) {
    SpringApplication.run(ExpenseSplitterApplication.class, args);
  }
}
