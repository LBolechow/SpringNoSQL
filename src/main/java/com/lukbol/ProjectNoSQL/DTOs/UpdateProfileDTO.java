package com.lukbol.ProjectNoSQL.DTOs;

public record UpdateProfileDTO(String name,
                               String surname,
                               String email,
                               String phoneNumber,
                               String password,
                               String repeatPassword) {
}
