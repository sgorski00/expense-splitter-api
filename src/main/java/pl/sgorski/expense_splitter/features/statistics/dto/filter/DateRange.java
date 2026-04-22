package pl.sgorski.expense_splitter.features.statistics.dto.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record DateRange(LocalDate from, LocalDate to) {
  public DateRange {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("From date must be before or equal to To date");
    }
  }

  public Instant fromInclusive() {
    return from.atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  public Instant toExclusive() {
    return to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
  }
}
