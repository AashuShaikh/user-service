package com.aashushaikh.user.dto;

import com.aashushaikh.user.model.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;
}
