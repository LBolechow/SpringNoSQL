package com.lukbol.ProjectNoSQL.Controllers;

import com.lukbol.ProjectNoSQL.DTOs.ApiResponseDTO;
import com.lukbol.ProjectNoSQL.DTOs.RegisterUserDTO;
import com.lukbol.ProjectNoSQL.DTOs.UpdateProfileDTO;
import com.lukbol.ProjectNoSQL.DTOs.UserDTO;
import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class UserController {
    private final UserService userService;

    @GetMapping("/user/details")
    public ResponseEntity<UserDTO> getUserDetails(Authentication authentication) {
        UserDTO userDTO = userService.getUserDetails(authentication);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/user/register")
    public ResponseEntity<ApiResponseDTO> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        ApiResponseDTO response = userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/apply")
    public ResponseEntity<ApiResponseDTO> changeProfile(
            Authentication authentication,
            @RequestBody UpdateProfileDTO updateProfileDTO
    ) {
        ApiResponseDTO response = userService.changeProfile(authentication, updateProfileDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<ApiResponseDTO> deleteUser(Authentication authentication) {
        ApiResponseDTO response = userService.deleteUser(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<ApiResponseDTO> logout(HttpServletRequest request) {
        ApiResponseDTO response = userService.logout(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/byProject/{projectId}")
    public ResponseEntity<List<UserDTO>> getUsersByProjectId(@PathVariable String projectId) {
        List<UserDTO> users = userService.getUsersByProjectId(projectId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/byTask/{taskId}")
    public ResponseEntity<List<UserDTO>> getUsersByTaskId(@PathVariable String taskId) {
        List<UserDTO> users = userService.getUsersByTaskId(taskId);
        return ResponseEntity.ok(users);
    }
}
