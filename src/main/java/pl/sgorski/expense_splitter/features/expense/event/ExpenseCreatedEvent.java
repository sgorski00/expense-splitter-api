package pl.sgorski.expense_splitter.features.expense.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record ExpenseCreatedEvent(
    UUID authorId,
    Set<UUID> participantsId,
    String name,
    @Nullable String description,
    BigDecimal amount,
    Instant createdAt) {}
