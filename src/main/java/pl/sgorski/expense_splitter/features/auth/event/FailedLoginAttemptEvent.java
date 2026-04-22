package pl.sgorski.expense_splitter.features.auth.event;

import java.time.Instant;

public record FailedLoginAttemptEvent(String email, Instant attemptedAt) {}
