package com.aashushaikh.user.controller;

import com.aashushaikh.user.dto.PublicUserResponse;
import com.aashushaikh.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> checkUserExists(@PathVariable String id) {
        userService.getPublicUserById(id);
        return ResponseEntity.ok().build();
    }

    // Called by the chat service to enrich chat member responses with profile data.
    // Returns PublicUserResponse — no email, no private fields.
    @GetMapping("/{id}/profile")
    public ResponseEntity<PublicUserResponse> getUserProfile(@PathVariable String id) {
        return ResponseEntity.ok(userService.getPublicUserById(id));
    }
}
