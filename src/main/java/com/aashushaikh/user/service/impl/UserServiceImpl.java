package com.aashushaikh.user.service.impl;

import com.aashushaikh.user.client.AuthServiceClient;
import com.aashushaikh.user.dto.*;
import com.aashushaikh.user.exception.DuplicateResourceException;
import com.aashushaikh.user.exception.UserNotFoundException;
import com.aashushaikh.user.model.User;
import com.aashushaikh.user.repository.UserRepository;
import com.aashushaikh.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        User user = User.builder()
                .id(request.getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .build();

        return toFullResponse(userRepository.save(user));
    }

    @Override
    @Cacheable(value = "my-profile", key = "#userId")
    public UserResponse getMyProfile(String userId) {
        return toFullResponse(findById(userId));
    }

    @Override
    @Cacheable(value = "public-users", key = "#id")
    public PublicUserResponse getPublicUserById(String id) {
        return toPublicResponse(findById(id));
    }

    @Override
    @Cacheable(value = "public-users-by-username", key = "#username")
    public PublicUserResponse getPublicUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return toPublicResponse(user);
    }

    @Override
    public List<PublicUserResponse> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query)
                .stream()
                .map(this::toPublicResponse)
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "my-profile", key = "#id"),
            @CacheEvict(value = "public-users", key = "#id"),
            @CacheEvict(value = "public-users-by-username", allEntries = true)
    })
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = findById(id);

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        if (request.getBio() != null) user.setBio(request.getBio());

        return toFullResponse(userRepository.save(user));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "my-profile", key = "#id"),
            @CacheEvict(value = "public-users", key = "#id"),
            @CacheEvict(value = "public-users-by-username", allEntries = true)
    })
    public UserResponse updateStatus(String id, UpdateStatusRequest request) {
        User user = findById(id);
        user.setStatus(request.getStatus());
        return toFullResponse(userRepository.save(user));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "my-profile", key = "#id"),
            @CacheEvict(value = "public-users", key = "#id"),
            @CacheEvict(value = "public-users-by-username", allEntries = true)
    })
    public void deleteUser(String id) {
        User user = findById(id);

        // deactivate credentials first — if this fails the profile is untouched
        authServiceClient.deactivateUser(id);

        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserResponse toFullResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .status(user.getStatus())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private PublicUserResponse toPublicResponse(User user) {
        return PublicUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .status(user.getStatus())
                .build();
    }
}
