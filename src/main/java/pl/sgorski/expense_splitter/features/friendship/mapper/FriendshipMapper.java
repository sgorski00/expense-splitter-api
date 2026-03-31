package pl.sgorski.expense_splitter.features.friendship.mapper;

import org.mapstruct.Mapper;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.dto.command.CreateFriendshipCommand;
import pl.sgorski.expense_splitter.features.friendship.dto.request.FriendshipRequest;
import pl.sgorski.expense_splitter.features.friendship.dto.response.FriendshipResponse;

@Mapper(config = CentralMapperConfig.class)
public interface FriendshipMapper {

    FriendshipResponse toResponse(Friendship friendship);

    CreateFriendshipCommand toCommand(FriendshipRequest request);
}
