package pl.sgorski.expense_splitter.features.user.dto.response;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record DetailedUserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role, //todo: change to enum/entity
        Instant createdAt,
        Instant updatedAt,
        @Nullable Instant deletedAt
) { }
