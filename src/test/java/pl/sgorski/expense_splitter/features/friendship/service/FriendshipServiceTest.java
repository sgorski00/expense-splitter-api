package pl.sgorski.expense_splitter.features.friendship.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.sgorski.expense_splitter.exceptions.friendship.FriendshipNotFoundException;
import pl.sgorski.expense_splitter.exceptions.friendship.InvalidFriendshipOperationException;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.friendship.dto.command.CreateFriendshipCommand;
import pl.sgorski.expense_splitter.features.friendship.repository.FriendshipRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

  @Mock private FriendshipRepository friendshipRepository;

  @Mock private UserService userService;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private FriendshipService friendshipService;

  private Friendship friendship;
  private User requester;
  private User recipient;

  @BeforeEach
  void setUp() {
    friendship = new Friendship();
    requester = new User();
    requester.setId(UUID.randomUUID());
    recipient = new User();
    recipient.setId(UUID.randomUUID());
    friendship.setRequester(requester);
    friendship.setRecipient(recipient);
  }

  @Test
  void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRequester() {
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    var result = friendshipService.getFriendshipForUser(friendship.getId(), requester);

    assertEquals(friendship, result);
  }

  @Test
  void getFriendshipForUser_shouldReturnFriendship_whenFriendshipExistsAndUserIsRecipient() {
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    var result = friendshipService.getFriendshipForUser(friendship.getId(), recipient);

    assertEquals(friendship, result);
  }

  @Test
  void
      getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipExistsAndUserIsNotParticipant() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    assertThrows(
        FriendshipNotFoundException.class,
        () -> friendshipService.getFriendshipForUser(friendship.getId(), otherUser));
  }

  @Test
  void getFriendshipForUser_shouldThrowFriendshipNotFoundException_whenFriendshipNotFound() {
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

    assertThrows(
        FriendshipNotFoundException.class,
        () -> friendshipService.getFriendshipForUser(friendship.getId(), recipient));
  }

  @Test
  void getFriendshipsByStatus_shouldReturnFriendships_whenRequestIsValid() {
    when(friendshipRepository.findFriendshipByUserAndStatus(any(), any(), any()))
        .thenReturn(Page.empty());

    var result =
        friendshipService.getFriendshipsByStatus(
            requester, FriendshipStatus.PENDING, Pageable.unpaged());

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
    var friendships = List.of(friendship);
    when(friendshipRepository.findFriends(any(), any())).thenReturn(new PageImpl<>(friendships));

    var result = friendshipService.getFriends(requester, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getFriends_shouldReturnUsers_whenOnlyRecipientsArePresent() {
    var friendships = List.of(friendship);
    when(friendshipRepository.findFriends(any(), any())).thenReturn(new PageImpl<>(friendships));

    var result = friendshipService.getFriends(recipient, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getFriends_shouldReturnUsers_whenBothArePresent() {
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
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));
    when(friendshipRepository.save(any())).thenReturn(friendship);

    var result =
        friendshipService.updateFriendshipStatus(
            friendship.getId(), recipient, FriendshipStatus.ACCEPTED);

    assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
    verify(friendshipRepository, times(1)).save(friendship);
  }

  @Test
  void updateFriendshipStatus_shouldThrowFriendshipNotFoundException_whenUserIsNotParticipant() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    assertThrows(
        FriendshipNotFoundException.class,
        () ->
            friendshipService.updateFriendshipStatus(
                friendship.getId(), otherUser, FriendshipStatus.ACCEPTED));
    assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
    verify(friendshipRepository, never()).save(any(Friendship.class));
  }

  @Test
  void updateFriendshipStatus_shouldThrowInvalidFriendshipOperationException_whenUserIsRequester() {
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    assertThrows(
        InvalidFriendshipOperationException.class,
        () ->
            friendshipService.updateFriendshipStatus(
                friendship.getId(), requester, FriendshipStatus.ACCEPTED));
    assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
    verify(friendshipRepository, never()).save(any(Friendship.class));
  }

  @Test
  void createFriendshipRequest_shouldCreateFriendship_whenRequestIsValid() {
    var command = new CreateFriendshipCommand(recipient.getId());
    when(userService.getUser(any(UUID.class))).thenReturn(recipient);
    when(friendshipRepository.save(any())).thenReturn(friendship);

    var result = friendshipService.createFriendshipRequest(requester, command);

    assertEquals(requester, result.getRequester());
    assertEquals(recipient, result.getRecipient());
    assertEquals(FriendshipStatus.PENDING, result.getStatus());
    verify(friendshipRepository, times(1)).save(any(Friendship.class));
  }

  @Test
  void
      createFriendshipRequest_shouldThrowInvalidFriendshipOperationException_whenUserIsRequestingThemselves() {
    var command = new CreateFriendshipCommand(requester.getId());

    assertThrows(
        InvalidFriendshipOperationException.class,
        () -> friendshipService.createFriendshipRequest(requester, command));
    verify(friendshipRepository, never()).save(any(Friendship.class));
  }

  @Test
  void deleteFriendship_shouldSoftDeleteFriendship_whenRequestIsValid() {
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));
    when(friendshipRepository.save(any())).thenReturn(friendship);

    friendshipService.deleteFriendship(friendship.getId(), requester);

    assertNotNull(friendship.getDeletedAt());
    verify(friendshipRepository, times(1)).save(friendship);
  }

  @Test
  void deleteFriendship_shouldThrowFriendshipNotFoundException_whenUserIsNotParticipant() {
    var otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    when(friendshipRepository.findByIdAndDeletedAtIsNull(any()))
        .thenReturn(Optional.of(friendship));

    assertThrows(
        FriendshipNotFoundException.class,
        () -> friendshipService.deleteFriendship(friendship.getId(), otherUser));
    assertNull(friendship.getDeletedAt());
    verify(friendshipRepository, never()).save(any(Friendship.class));
  }

  @Test
  void areFriends_shouldReturnTrue_whenIdsAreEmpty() {
    var result = friendshipService.areFriends(new User(), List.of());

    assertTrue(result);
  }

  @Test
  void areFriends_shouldReturnTrue_whenAllIdsAreFriends() {
    when(friendshipRepository.countAcceptedFriends(any(), any())).thenReturn(2L);

    var result =
        friendshipService.areFriends(new User(), List.of(UUID.randomUUID(), UUID.randomUUID()));

    assertTrue(result);
  }

  @Test
  void areFriends_shouldReturnFalse_whenNotAllIdsAreFriends() {
    when(friendshipRepository.countAcceptedFriends(any(), any())).thenReturn(1L);

    var result =
        friendshipService.areFriends(new User(), List.of(UUID.randomUUID(), UUID.randomUUID()));

    assertFalse(result);
  }
}
