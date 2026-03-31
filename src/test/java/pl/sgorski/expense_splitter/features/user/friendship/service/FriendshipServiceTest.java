package pl.sgorski.expense_splitter.features.user.friendship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.expense_splitter.features.friendship.repository.FriendshipRepository;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipService friendshipService;

    @Test
    void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRequester() {

    }

    @Test
    void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRecipient() {

    }

    @Test
    void getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipExistsAndUserIsNotParticipant() {

    }

    @Test
    void getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipNotFound() {

    }

    @Test
    void getFriendshipsByStatus_shouldReturnFriendships_whenRequestIsValid() {

    }

    @Test
    void getFriends_shouldReturnUsers_whenOnlyRequestersArePresent() {

    }

    @Test
    void getFriends_shouldReturnUsers_whenOnlyRecipientsArePresent() {

    }

    @Test
    void getFriends_shouldReturnUsers_whenBothArePresent() {

    }

    @Test
    void updateFriendshipStatus_shouldUpdateStatus_whenRequestIsValid() {

    }

    @Test
    void updateFriendshipStatus_shouldThrowFriendshipNotFoundException_whenUserIsNotParticipant() {

    }

    @Test
    void updateFriendshipStatus_shouldThrowInvalidFriendshipOperationException_whenUserIsRequester() {

    }

    @Test
    void createFriendshipRequest_shouldCreateFriendship_whenRequestIsValid() {

    }

    @Test
    void createFriendshipRequest_shouldThrowInvalidFriendshipOperationException_whenUserIsRequestingThemselves() {

    }

    @Test
    void deleteFriendship_shouldSoftDeleteFriendship_whenRequestIsValid() {

    }

    @Test
    void deleteFriendship_shouldThrowFriendshipNotFoundException_whenUserIsNotParticipant() {

    }
}
