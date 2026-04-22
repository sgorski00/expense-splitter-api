package pl.sgorski.expense_splitter.features.statistics.dto.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import pl.sgorski.expense_splitter.exceptions.DomainObjectValidationException;

public class DateRangeTest {

  @Test
  void constructor_shouldThrow_whenFromDateIsAfterToDate() {
    var from = LocalDate.of(2026, 1, 2);
    var to = LocalDate.of(2026, 1, 1);

    assertThrows(DomainObjectValidationException.class, () -> new DateRange(from, to));
  }

  @Test
  void fromInclusive_shouldReturnStartOfDayInstant() {
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 2);
    var dateRange = new DateRange(from, to);

    var expected = from.atStartOfDay().toInstant(ZoneOffset.UTC);
    var actual = dateRange.fromInclusive();

    assertEquals(expected, actual);
  }

  @Test
  void toExclusive_shouldReturnStartOfNextDayInstant() {
    var from = LocalDate.of(2026, 1, 1);
    var to = LocalDate.of(2026, 1, 2);
    var dateRange = new DateRange(from, to);

    var expected = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    var actual = dateRange.toExclusive();

    assertEquals(expected, actual);
  }
}
