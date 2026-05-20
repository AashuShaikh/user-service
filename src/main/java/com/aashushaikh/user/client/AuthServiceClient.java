package com.aashushaikh.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth", path = "/auth/internal")
public interface AuthServiceClient {

    @PatchMapping("/users/{id}/deactivate")
    void deactivateUser(@PathVariable String id);
}
