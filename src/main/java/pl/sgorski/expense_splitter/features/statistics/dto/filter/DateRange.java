package pl.sgorski.expense_splitter.features.statistics.dto.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import pl.sgorski.expense_splitter.exceptions.DomainObjectValidationException;

public record DateRange(LocalDate from, LocalDate to) {
  public DateRange {
    if (from.isAfter(to)) {
      throw new DomainObjectValidationException("from must be <= to");
    }
  }

  public Instant fromInclusive() {
    return from.atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  public Instant toExclusive() {
    return to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
  }
}
