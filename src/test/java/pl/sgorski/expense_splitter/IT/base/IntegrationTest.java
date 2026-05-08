package pl.sgorski.expense_splitter.IT.base;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private Flyway flyway;
  @Autowired protected Cache<String, Bucket> rateLimitCache;

  protected RestTestClient restTestClient;

  @BeforeEach
  void setUpBindClientToServer() {
    restTestClient =
        RestTestClient.bindToServer()
            .baseUrl("http://localhost:" + port + "/api")
            .defaultHeader(apiVersionHeaderName, "v1.0.0")
            .build();
  }

  @BeforeEach
  void cleanUp() {
    rateLimitCache.invalidateAll();
    flyway.clean();
    flyway.migrate();
  }
}
