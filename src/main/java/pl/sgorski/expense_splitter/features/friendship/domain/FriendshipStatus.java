package pl.sgorski.expense_splitter.features.friendship.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.sgorski.expense_splitter.exceptions.FriendhipStatusNotFoundException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FriendshipStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),;

    private final String displayName;

    @JsonCreator
    public static FriendshipStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new FriendhipStatusNotFoundException(value));
    }
}
