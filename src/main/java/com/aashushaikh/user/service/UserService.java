package com.aashushaikh.user.service;

import com.aashushaikh.user.dto.*;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getMyProfile(String userId);

    PublicUserResponse getPublicUserById(String id);

    PublicUserResponse getPublicUserByUsername(String username);

    List<PublicUserResponse> searchUsers(String query);

    UserResponse updateUser(String id, UpdateUserRequest request);

    UserResponse updateStatus(String id, UpdateStatusRequest request);

    void deleteUser(String id);
}
