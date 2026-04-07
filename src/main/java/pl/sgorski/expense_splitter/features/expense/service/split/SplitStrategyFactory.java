package pl.sgorski.expense_splitter.features.expense.service.split;

import java.util.Map;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.expense.domain.SplitType;
import pl.sgorski.expense_splitter.features.expense.service.split.impl.EqualSplitStrategy;

@Component
public final class SplitStrategyFactory {

  private final Map<SplitType, SplitStrategy> strategies;

  public SplitStrategyFactory(EqualSplitStrategy equalSplitStrategy) {
    this.strategies = Map.of(SplitType.EQUAL, equalSplitStrategy);
  }

  public SplitStrategy get(SplitType type) {
    return strategies.get(type);
  }
}
