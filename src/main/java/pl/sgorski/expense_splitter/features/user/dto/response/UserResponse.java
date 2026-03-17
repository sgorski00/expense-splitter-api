package pl.sgorski.expense_splitter.features.user.dto.response;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String role, //todo: change to enum/entity
        Instant createdAt
) { }
