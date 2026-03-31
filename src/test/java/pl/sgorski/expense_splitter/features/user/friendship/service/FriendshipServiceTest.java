package pl.sgorski.expense_splitter.features.user.friendship.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sgorski.expense_splitter.exceptions.FriendshipNotFoundException;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.friendship.repository.FriendshipRepository;
import pl.sgorski.expense_splitter.features.friendship.service.FriendshipService;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipService friendshipService;

    private Friendship friendship;
    private User requester;
    private User recipient;

    @BeforeEach
    void setUp() {
        friendship = new Friendship();
        requester = new User();
        recipient = new User();
        friendship.setRequester(requester);
        friendship.setRecipient(recipient);
    }

    @Test
    void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRequester() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        when(friendshipRepository.findById(any())).thenReturn(Optional.of(friendship));

        var result = friendshipService.getFriendshipForUser(friendship.getId(), requester);

        assertEquals(friendship, result);
    }

    @Test
    void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRecipient() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        when(friendshipRepository.findById(any())).thenReturn(Optional.of(friendship));

        var result = friendshipService.getFriendshipForUser(friendship.getId(), recipient);

        assertEquals(friendship, result);
    }

    @Test
    void getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipExistsAndUserIsNotParticipant() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        var otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        when(friendshipRepository.findById(any())).thenReturn(Optional.of(friendship));

        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.getFriendshipForUser(friendship.getId(), otherUser));
    }

    @Test
    void getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipNotFound() {
        when(friendshipRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.getFriendshipForUser(friendship.getId(), recipient));
    }

    @Test
    void getFriendshipsByStatus_shouldReturnFriendships_whenRequestIsValid() {
        when(friendshipRepository.findFriendshipByUserAndStatus(any(), any(), any())).thenReturn(Page.empty());

        var result = friendshipService.getFriendshipsByStatus(requester, FriendshipStatus.PENDING, Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void getFriends_shouldReturnEmptyUsers_whenNoFriendsFound() {
        when(friendshipRepository.findFriends(any(), any())).thenReturn(Page.empty());

        var result = friendshipService.getFriends(new User(), Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getFriends_shouldReturnUsers_whenOnlyRequestersArePresent() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        var friendships = List.of(friendship);
        when(friendshipRepository.findFriends(any(), any())).thenReturn(new PageImpl<>(friendships));

        var result = friendshipService.getFriends(requester, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getFriends_shouldReturnUsers_whenOnlyRecipientsArePresent() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        var friendships = List.of(friendship);
        when(friendshipRepository.findFriends(any(), any())).thenReturn(new PageImpl<>(friendships));

        var result = friendshipService.getFriends(recipient, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getFriends_shouldReturnUsers_whenBothArePresent() {
        requester.setId(UUID.randomUUID());
        recipient.setId(UUID.randomUUID());
        var friendship2 = new Friendship();
        var recipient2 = new User();
        recipient2.setId(UUID.randomUUID());
        friendship2.setRequester(recipient);
        friendship2.setRecipient(recipient2);
        var friendships = List.of(friendship, friendship2);
        when(friendshipRepository.findFriends(any(), any())).thenReturn(new PageImpl<>(friendships));

        var result = friendshipService.getFriends(recipient, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
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
