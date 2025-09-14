package com.lukbol.ProjectNoSQL.Controllers;

import com.lukbol.ProjectNoSQL.DTOs.ApiResponseDTO;
import com.lukbol.ProjectNoSQL.DTOs.AuthenticateRequestDTO;
import com.lukbol.ProjectNoSQL.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService=userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO> authenticateUser(@RequestBody AuthenticateRequestDTO requestDTO) {
        ApiResponseDTO responseDTO = userService.authenticateUser(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }


}

