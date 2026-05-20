package com.aashushaikh.user.dto;

import com.aashushaikh.user.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PublicUserResponse implements Serializable {

    private String id;
    private String username;
    private String displayName;
    private String profilePicture;
    private String bio;
    private UserStatus status;
}
