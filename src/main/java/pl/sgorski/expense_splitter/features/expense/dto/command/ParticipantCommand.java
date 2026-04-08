package pl.sgorski.expense_splitter.features.expense.dto.command;

import java.math.BigDecimal;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record ParticipantCommand(
    UUID userId, @Nullable BigDecimal amount, @Nullable BigDecimal percentage) {}
