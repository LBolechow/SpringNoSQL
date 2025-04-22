package com.lukbol.ProjectNoSQL.Services;

import com.lukbol.ProjectNoSQL.Models.BlacklistedToken;
import com.lukbol.ProjectNoSQL.Models.Role;
import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Repositories.BlacklistedTokenRepository;
import com.lukbol.ProjectNoSQL.Repositories.RoleRepository;
import com.lukbol.ProjectNoSQL.Repositories.UserRepository;
import com.lukbol.ProjectNoSQL.Utils.JwtUtil;
import com.lukbol.ProjectNoSQL.Utils.UserUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserUtils userUtils;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public ResponseEntity<Map<String, Object>> authenticateUser(String usernameOrEmail,
                                                                String password) {
        String username;
        try {
            if (usernameOrEmail.contains("@") && usernameOrEmail.contains(".")) {
                User userByEmail = userRepository.findByEmail(usernameOrEmail);
                if (userByEmail == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Collections.singletonMap("message", "Nie znaleziono użytkownika o takim adresie email."));
                }
                username = userByEmail.getUsername();
            } else {
                username = usernameOrEmail;
            }
            User user = userRepository.findOptionalByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Brak użytkownika z taką nazwą: " + username));



            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtil.generateToken(username);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("redirectUrl", "http://localhost:8080/main");
            response.put("username", username);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Błędna nazwa użytkownika/email lub hasło."));
        }
    }

    public ResponseEntity<Map<String, Object>> registerUser(String username, String name, String surname, String email , String phoneNumber, String password) {

        if (userUtils.emailExists(email)) {
            return userUtils.createErrorResponse("Użytkownik o takim adresie email już istnieje.");
        }

        if (userUtils.phoneNumberExists(phoneNumber)) {
            return userUtils.createErrorResponse("Użytkownik o takim numerze telefonu już istnieje.");
        }
        if (userUtils.usernameExists(username)) {
            return userUtils.createErrorResponse("Użytkownik o takiej nazwie użytkownika już istnieje.");
        }

        if (!userUtils.isValidPassword(password)) {
            return userUtils.createErrorResponse("Hasło musi spełniać określone kryteria bezpieczeństwa.");
        }


        User regUser = new User(name, surname, email, phoneNumber, passwordEncoder.encode(password), username, false);

        //Automatycznie nadaję rolę Client podczas rejestracji.
        Role role = roleRepository.findByName("ROLE_CLIENT");
        regUser.setRoles(Arrays.asList(role));

        try {
            userRepository.save(regUser);
        } catch (DataAccessException e) {
            return userUtils.createErrorResponse("Błąd: " + e.getMessage());
        }

        return userUtils.createSuccessResponse("Poprawnie utworzono konto.");
    }

    public ResponseEntity<User> getUserDetails(Authentication authentication)
    {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        String username = ((UserDetails)principal).getUsername();

        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));



        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Map<String, Object>> changeProfile(Authentication authentication,
                                                             String name,
                                                             String surname,
                                                             String email,
                                                             String phoneNumber,
                                                             String password,
                                                             String repeatPassword) {

        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (userUtils.isNullOrEmpty(name) || userUtils.isNullOrEmpty(surname) || userUtils.isNullOrEmpty(email) || userUtils.isNullOrEmpty(phoneNumber)) {
            return userUtils.createErrorResponse("Wszystkie wartości muszą być wypełnione.");
        }

        if (userUtils.isNullOrEmpty(password) && !userUtils.isNullOrEmpty(repeatPassword)) {
            return userUtils.createErrorResponse("Hasła są puste.");
        }

        if (!password.equals(repeatPassword)) {
            return userUtils.createErrorResponse("Hasła nie są takie same.");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return userUtils.createErrorResponse("Nowe hasło jest takie samo jak poprzednie.");
        }
        user.setPassword(passwordEncoder.encode(password));

        try {
            user.setName(name);
            user.setSurname(surname);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            userRepository.save(user);
        } catch (DataAccessException e) {
            return userUtils.createErrorResponse("Błąd: " + e.getMessage());
        }

        return userUtils.createSuccessResponse("Poprawnie zapisano zmiany.");
    }

    public ResponseEntity<Map<String, Object>> deleteUser(Authentication authentication)
    {

        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika z nazwą: " + username));

        try
        {
            userRepository.delete(user);
        }
        catch (DataAccessException e)
        {
            return userUtils.createErrorResponse("Błąd: " + e.getMessage());
        }

        return userUtils.createSuccessResponse("Poprawnie usunięto konto.");
    }


    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String token = jwtUtil.extractJwtFromRequest(request);

        if (token == null) {
            return userUtils.createErrorResponse("Nie znaleziono tokenu");
        }
        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(token);
        } catch (DataAccessException e) {
            return userUtils.createErrorResponse("Błąd: " + e.getMessage());
        }

        Date issuedAt = claims.getIssuedAt();
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, issuedAt);
        blacklistedTokenRepository.save(blacklistedToken);

        //Przekierowanie do strony login po stronie frontendu.
        return userUtils.createSuccessResponse("Wylogowano pomyślnie");

    }
}
