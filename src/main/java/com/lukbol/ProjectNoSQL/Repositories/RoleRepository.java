package com.lukbol.ProjectNoSQL.Repositories;

import com.lukbol.ProjectNoSQL.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, Long> {
    Role findByName(String name);
}

