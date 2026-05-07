package pl.sgorski.expense_splitter.IT.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.sgorski.expense_splitter.IT.config.TestContainersConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
public abstract class IntegrationTest {

  @Value("${spring.mvc.apiversion.use.header}")
  private String apiVersionHeaderName;

  @LocalServerPort private int port;

  protected RestTestClient restTestClient;

  @BeforeEach
  void setUpBindClientToServer() {
    restTestClient =
        RestTestClient.bindToServer()
            .baseUrl("http://localhost:" + port + "/api")
            .defaultHeader(apiVersionHeaderName, "v1.0.0")
            .build();
  }
}
