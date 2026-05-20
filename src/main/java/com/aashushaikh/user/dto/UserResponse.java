package com.aashushaikh.user.dto;

import com.aashushaikh.user.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse implements Serializable {

    private String id;
    private String username;
    private String email;
    private String displayName;
    private String profilePicture;
    private String bio;
    private UserStatus status;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
