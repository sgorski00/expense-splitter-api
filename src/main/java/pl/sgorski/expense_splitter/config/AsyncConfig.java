package pl.sgorski.expense_splitter.config;

import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
  public AsyncTaskExecutor taskExecutor() {
    var executor = Executors.newVirtualThreadPerTaskExecutor();
    var taskExecutorAdapter = new TaskExecutorAdapter(executor);
    return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutorAdapter);
  }
}
