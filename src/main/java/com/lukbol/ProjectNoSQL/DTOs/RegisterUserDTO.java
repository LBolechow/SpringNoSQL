package com.lukbol.ProjectNoSQL.DTOs;

public record RegisterUserDTO(String username,
                              String name,
                              String surname,
                              String email,
                              String phoneNumber,
                              String password) {
}
