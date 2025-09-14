package com.lukbol.ProjectNoSQL.DTOs;

public record UserDTO (String id,
                       String username,
                       String name,
                       String surname,
                       String email,
                       String phoneNumber) {
}
