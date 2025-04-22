package com.lukbol.ProjectNoSQL;

import com.lukbol.ProjectNoSQL.Models.BlacklistedToken;
import com.lukbol.ProjectNoSQL.Models.Role;
import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Repositories.RoleRepository;
import com.lukbol.ProjectNoSQL.Repositories.UserRepository;
import com.lukbol.ProjectNoSQL.Repositories.BlacklistedTokenRepository;
import com.lukbol.ProjectNoSQL.Services.UserService;
import com.lukbol.ProjectNoSQL.Utils.JwtUtil;
import com.lukbol.ProjectNoSQL.Utils.UserUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class userServiceTests {
    @Mock
    private Authentication authentication;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;
    @Mock
    private UserUtils userUtils;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public userServiceTests() {
    }

    @Test
    public void testAuthenticateUser() {
        String usernameOrEmail = "test@test.com";
        String password = "Password123!";
        String username = "testuser";
        String token = "mockedToken";

        User user = new User("Jan", "Kowalski", usernameOrEmail, "123456789", "encodedPassword", username, true);
        when(userRepository.findByEmail(usernameOrEmail)).thenReturn(user);
        when(userRepository.findOptionalByUsername(username)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(username)).thenReturn(token);

        ResponseEntity<Map<String, Object>> response = userService.authenticateUser(usernameOrEmail, password);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(token, body.get("token"));
        assertEquals("http://localhost:8080/main", body.get("redirectUrl"));
        assertEquals(username, body.get("username"));

        verify(userRepository).findByEmail(usernameOrEmail);
        verify(userRepository).findOptionalByUsername(username);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test
    public void testRegisterUser() {

        when(userUtils.emailExists("test@test.com")).thenReturn(false);
        when(userUtils.phoneNumberExists("123456789")).thenReturn(false);
        when(userUtils.usernameExists("testuser")).thenReturn(false);
        when(userUtils.isValidPassword("Password123!")).thenReturn(true);

        Role role = mock(Role.class);
        when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(role);

        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");

        User user = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(userUtils.createSuccessResponse(anyString()))
                .thenAnswer(invocation -> {
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("message", "Poprawnie utworzono konto.");
                    return ResponseEntity.ok(successResponse);
                });

        ResponseEntity<Map<String, Object>> response = userService.registerUser("testuser", "Jan", "Kowalski", "test@test.com", "123456789", "Password123!");


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Poprawnie utworzono konto.", response.getBody().get("message"));


        verify(userRepository).save(any(User.class));
        verify(userUtils).createSuccessResponse("Poprawnie utworzono konto.");
    }

    @Test
    public void testGetUserDetails() {

        String username = "testuser";
        User user = mock(User.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);

        when(userRepository.findOptionalByUsername(username)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userService.getUserDetails(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());


        verify(authentication).getPrincipal();
        verify(userDetails).getUsername();
        verify(userRepository).findOptionalByUsername(username);
    }

    @Test
    public void testChangeProfile() {
        String username = "testuser";
        String name = "Jan";
        String surname = "Kowalski";
        String email = "test@test.com";
        String phoneNumber = "123456789";
        String password = "Password123!";
        String repeatPassword = "Password123!";

        User user = mock(User.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userRepository.findOptionalByUsername(username)).thenReturn(Optional.of(user));
        when(userUtils.isNullOrEmpty(anyString())).thenReturn(false);


        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("newEncodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);


        when(userUtils.createSuccessResponse(anyString()))
                .thenAnswer(invocation -> {
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("success", true);
                    successResponse.put("message", "Poprawnie zapisano zmiany.");
                    return ResponseEntity.ok(successResponse);
                });

        ResponseEntity<Map<String, Object>> response = userService.changeProfile(authentication, name, surname, email, phoneNumber, password, repeatPassword);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Poprawnie zapisano zmiany.", response.getBody().get("message"));

        verify(userRepository).save(user);
        verify(userUtils).createSuccessResponse("Poprawnie zapisano zmiany.");
    }
    @Test
    public void testDeleteUser() {
        String username = "validUsername";
        User user = mock(User.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userRepository.findOptionalByUsername(username)).thenReturn(Optional.of(user));

        when(userUtils.createSuccessResponse("Poprawnie usunięto konto."))
                .thenAnswer(invocation -> {
                    Map<String, Object> successResponse = Map.of(
                            "success", true,
                            "message", "Poprawnie usunięto konto."
                    );
                    return ResponseEntity.ok(successResponse);
                });

        ResponseEntity<Map<String, Object>> response = userService.deleteUser(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("Poprawnie usunięto konto.", body.get("message"));

        verify(userRepository).findOptionalByUsername(username);
        verify(userRepository).delete(user);
        verify(userUtils).createSuccessResponse("Poprawnie usunięto konto.");
    }
    @Test
    public void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String token = "mockedToken";
        when(jwtUtil.extractJwtFromRequest(request)).thenReturn(token);

        Claims claims = mock(Claims.class);
        Date issuedAt = new Date();
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);
        when(claims.getIssuedAt()).thenReturn(issuedAt);

        BlacklistedToken blacklistedToken = new BlacklistedToken(token, issuedAt);
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class))).thenReturn(blacklistedToken);


        when(userUtils.createSuccessResponse("Wylogowano pomyślnie"))
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("message", "Wylogowano pomyślnie")));


        ResponseEntity<Map<String, Object>> response = userService.logout(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Wylogowano pomyślnie", response.getBody().get("message"));


        verify(jwtUtil).extractJwtFromRequest(request);
        verify(jwtUtil).extractAllClaims(token);
        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
        verify(userUtils).createSuccessResponse("Wylogowano pomyślnie");
    }

}
