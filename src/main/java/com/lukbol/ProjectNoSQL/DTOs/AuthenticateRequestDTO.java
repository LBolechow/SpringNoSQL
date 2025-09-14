package com.lukbol.ProjectNoSQL.DTOs;

public record AuthenticateRequestDTO(String usernameOrEmail, String password) {
}
