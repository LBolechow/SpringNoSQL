package com.lukbol.ProjectNoSQL.Controllers;

import com.lukbol.ProjectNoSQL.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService=userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestParam String usernameOrEmail,
                                                                @RequestParam String password) {
        return userService.authenticateUser(usernameOrEmail, password);
    }


}

