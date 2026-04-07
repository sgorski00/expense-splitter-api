package pl.sgorski.expense_splitter.features.user.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sgorski.expense_splitter.exceptions.not_found.UserNotFoundException;
import pl.sgorski.expense_splitter.features.auth.refresh_token.service.RefreshTokenService;
import pl.sgorski.expense_splitter.features.user.domain.Role;
import pl.sgorski.expense_splitter.features.user.domain.User;
import pl.sgorski.expense_splitter.features.user.dto.command.CreateUserCommand;
import pl.sgorski.expense_splitter.features.user.dto.command.UpdateUserCommand;
import pl.sgorski.expense_splitter.features.user.mapper.UserMapper;
import pl.sgorski.expense_splitter.features.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;
  private final UserIdentityService userIdentityService;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  public User getUser(String email) {
    return userRepository
        .findByEmailAndDeletedAtIsNull(email)
        .orElseThrow(() -> new UserNotFoundException(email));
  }

  public User getUser(UUID id) {
    return userRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public User getUserWithIdentities(UUID id) {
    return userRepository
        .findWithIdentitiesByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public boolean isUserPresent(String email) {
    return userRepository.existsByEmailAndDeletedAtIsNull(email);
  }

  @Transactional
  public void deleteUser(User user) {
    refreshTokenService.revokeAllUserTokens(user.getId());
    userIdentityService.removeAllUserIdentities(user);
    userRepository.delete(user);
  }

  public boolean isAdminPresent() {
    return userRepository.countByRole(Role.ADMIN) > 0;
  }

  public Page<User> searchUsers(String query, Pageable pageable) {
    return userRepository.findAllByQuery(query, pageable);
  }

  public Page<User> searchUsersAdmin(
      @Nullable String query, @Nullable Role role, Pageable pageable) {
    var typeSafeQuery = query != null ? query : "";
    return userRepository.findAllByQueryAndRole(typeSafeQuery, role, pageable);
  }

  @Transactional
  public User createUser(CreateUserCommand command) {
    var user = userMapper.toUser(command);
    user.setPasswordHash(passwordEncoder.encode(command.password()));
    user.setPasswordForChange(true);
    return userRepository.save(user);
  }

  @Transactional
  public User updateUser(UUID id, UpdateUserCommand command) {
    var user = getUser(id);
    userMapper.updateUser(command, user);
    return userRepository.save(user);
  }

  @Transactional
  public User changePassword(UUID id, String rawPassword) {
    var user = getUser(id);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setPasswordForChange(true);
    return userRepository.save(user);
  }

  public List<User> getUsers(Set<UUID> ids) {
    return userRepository.findAllByIdInAndDeletedAtIsNull(ids);
  }
}
