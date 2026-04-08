package pl.sgorski.expense_splitter.features.expense.service.split;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.expense.service.split.impl.EqualSplitStrategy;

@ExtendWith(MockitoExtension.class)
public class SplitStrategyFactoryTest {

  @Mock private EqualSplitStrategy equalSplitStrategy;

  @InjectMocks private SplitStrategyFactory factory;

  @Test
  void get_shouldReturnEqualSplitStrategy_whenTypeIsEqual() {
    var strategy = factory.get(SplitType.EQUAL);

    assertInstanceOf(EqualSplitStrategy.class, strategy);
  }
}
