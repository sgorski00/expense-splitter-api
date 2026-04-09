package pl.sgorski.expense_splitter.features.payment.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(UUID expenseId, BigDecimal amount) {}
