package com.aashushaikh.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String displayName;

    private String profilePicture;

    @Size(max = 300, message = "Bio must not exceed 300 characters")
    private String bio;
}
