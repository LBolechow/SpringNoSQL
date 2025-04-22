package com.lukbol.ProjectNoSQL.Utils;

import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;


    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber) != null;
    }
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    public boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }



    public ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
    public ResponseEntity<Map<String, Object>> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    public boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }


}
