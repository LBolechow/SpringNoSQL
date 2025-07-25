package com.lukbol.ProjectNoSQL.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String id; 

    private String token;

    private Date expiresAt;

    public BlacklistedToken() {}

    public BlacklistedToken(String token, Date expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
