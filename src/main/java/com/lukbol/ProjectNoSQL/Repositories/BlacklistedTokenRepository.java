package com.lukbol.ProjectNoSQL.Repositories;

import com.lukbol.ProjectNoSQL.Models.BlacklistedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findOptionalByToken(String token);
}
