package pl.sgorski.expense_splitter.features.friendship.service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.sgorski.expense_splitter.exceptions.friendship.InvalidFriendshipOperationException;
import pl.sgorski.expense_splitter.exceptions.friendship.FriendshipNotFoundException;
import pl.sgorski.expense_splitter.features.friendship.domain.Friendship;
import pl.sgorski.expense_splitter.features.friendship.domain.FriendshipStatus;
import pl.sgorski.expense_splitter.features.friendship.dto.command.CreateFriendshipCommand;
import pl.sgorski.expense_splitter.features.friendship.repository.FriendshipRepository;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.service.UserService;

@RequiredArgsConstructor
@Service
@Slf4j
public class FriendshipService {

  private final FriendshipRepository friendshipRepository;
  private final UserService userService;

  public Friendship getFriendshipForUser(UUID id, User user) {
    log.debug("Fetching friendship with id: {} for user: {}", id, user.getId());
    var friendship =
        friendshipRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new FriendshipNotFoundException(id));

    if (!friendship.getRequester().equals(user) && !friendship.getRecipient().equals(user)) {
      log.warn("User {} is not part of friendship {}. Access denied.", user.getId(), id);
      throw new FriendshipNotFoundException(id);
    }
    return friendship;
  }

  public Page<Friendship> getFriendshipsByStatus(
      User user, FriendshipStatus status, Pageable pageable) {
    log.debug("Fetching friendships for user: {} with status: {}", user.getId(), status);
    return friendshipRepository.findFriendshipByUserAndStatus(user, status, pageable);
  }

  public Page<User> getFriends(User user, Pageable pageable) {
    log.debug("Fetching friends for user: {}", user.getId());
    return friendshipRepository
        .findFriends(user, pageable)
        .map(f -> f.getRequester().equals(user) ? f.getRecipient() : f.getRequester());
  }

  public boolean areFriends(User user, Collection<UUID> ids) {
    log.debug("Checking friendship status between user: {} and users: {}", user.getId(), ids);
    if (CollectionUtils.isEmpty(ids)) return true;
    var count = friendshipRepository.countAcceptedFriends(user, ids);
    return count == ids.size();
  }

  @Transactional
  public Friendship updateFriendshipStatus(UUID id, User user, FriendshipStatus status) {
    log.debug("Updating friendship {} to status: {} by user: {}", id, status, user.getId());
    var friendship = getFriendshipForUser(id, user);
    if (!friendship.getRecipient().equals(user)) {
      throw new InvalidFriendshipOperationException(
          "Only the recipient can update the friendship status.");
    }
    friendship.changeStatus(status);
    var updated = friendshipRepository.save(friendship);
    log.info("Friendship {} status updated to {}", id, status);
    return updated;
  }

  @Transactional
  public Friendship createFriendshipRequest(User requester, CreateFriendshipCommand command) {
    log.debug(
        "Creating friendship request from user: {} to user: {}",
        requester.getId(),
        command.recipientId());
    if (requester.getId().equals(command.recipientId())) {
      log.warn("User {} cannot send friend request to themselves.", requester.getId());
      throw new InvalidFriendshipOperationException("Cannot send friend request to yourself.");
    }
    var recipient = userService.getUser(command.recipientId());
    var friendship = new Friendship();
    friendship.setRequester(requester);
    friendship.setRecipient(recipient);
    var created = friendshipRepository.save(friendship);
    log.info(
        "Friendship request created. ID: {}, from: {}, to: {}",
        created.getId(),
        requester.getId(),
        recipient.getId());
    return created;
  }

  @Transactional
  public void deleteFriendship(UUID id, User user) {
    log.debug("Deleting friendship {} by user: {}", id, user.getId());
    var friendship = getFriendshipForUser(id, user);
    friendship.delete();
    friendshipRepository.save(friendship);
    log.info("Friendship {} soft deleted by user: {}", id, user.getId());
  }
}
