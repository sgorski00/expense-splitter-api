package pl.sgorski.expense_splitter.features.friendship.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;

@Component
public final class FriendshipStatusConverter implements Converter<String, FriendshipStatus> {
    @Override
    public FriendshipStatus convert(String source) {
        return FriendshipStatus.fromString(source);
    }
}
