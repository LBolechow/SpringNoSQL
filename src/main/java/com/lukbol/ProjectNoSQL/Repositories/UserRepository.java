package com.lukbol.ProjectNoSQL.Repositories;

import com.lukbol.ProjectNoSQL.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    Optional<User> findOptionalByEmail(String email);
    User findByUsername(String username);
    Optional<User> findOptionalByUsername(String usernameOrEmail);

    void deleteUserByUsername(String testUsername);
}
