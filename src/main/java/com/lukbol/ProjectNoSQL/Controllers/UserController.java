package com.lukbol.ProjectNoSQL.Controllers;

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

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/userDetails")
    public ResponseEntity<User> getUserDetails(Authentication authentication) {
        return userService.getUserDetails(authentication);
    }

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestParam("username") String username,
                                                            @RequestParam("name") String name,
                                                            @RequestParam("surname") String surname,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("phoneNumber") String phoneNumber,
                                                            @RequestParam("password") String password
    ) {
        return userService.registerUser(username, name, surname, email ,phoneNumber, password);
    }
    @PutMapping("/user/apply")
    public ResponseEntity<Map<String, Object>> changeProfile(Authentication authentication, @RequestParam("name") String name,
                                                             @RequestParam("surname") String surname,
                                                             @RequestParam("email") String email,
                                                             @RequestParam("phoneNumber") String phoneNumber,
                                                             @RequestParam("password") String password,
                                                             @RequestParam("repeatPassword") String repeatPassword)
    {
        return userService.changeProfile(authentication, name, surname, email, phoneNumber, password, repeatPassword);
    }
    @DeleteMapping("/user/deleteUser")
    public ResponseEntity<Map<String, Object>> deleteUser(Authentication authentication) {
        return userService.deleteUser(authentication);
    }
    @PostMapping("/user/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        return userService.logout(request);
    }
    @GetMapping("/user/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/getUserById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/byProject/{projectId}")
    public ResponseEntity<List<User>> getUsersByProjectId(@PathVariable String projectId) {
        List<User> users = userService.getUsersByProjectId(projectId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/byTask/{taskId}")
    public ResponseEntity<List<User>> getUsersByTaskId(@PathVariable String taskId) {
        List<User> users = userService.getUsersByTaskId(taskId);
        return ResponseEntity.ok(users);
    }
}
