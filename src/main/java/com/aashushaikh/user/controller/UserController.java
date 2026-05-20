package com.aashushaikh.user.controller;

import com.aashushaikh.user.dto.*;
import com.aashushaikh.user.dto.ErrorCode;
import com.aashushaikh.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User profile created", userService.createUser(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", userService.getMyProfile(jwt.getSubject())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicUserResponse>> getPublicUser(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched", userService.getPublicUserById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<PublicUserResponse>> getPublicUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success("User fetched", userService.getPublicUserByUsername(username)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PublicUserResponse>>> searchUsers(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Search query must not be empty", ErrorCode.BAD_REQUEST));
        }
        return ResponseEntity.ok(ApiResponse.success("Search results", userService.searchUsers(q)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request) {
        if (!id.equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only update your own profile", ErrorCode.FORBIDDEN));
        }
        return ResponseEntity.ok(ApiResponse.success("User updated", userService.updateUser(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateStatusRequest request) {
        if (!id.equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only update your own status", ErrorCode.FORBIDDEN));
        }
        return ResponseEntity.ok(ApiResponse.success("Status updated", userService.updateStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {
        if (!id.equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("You can only delete your own account", ErrorCode.FORBIDDEN));
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
