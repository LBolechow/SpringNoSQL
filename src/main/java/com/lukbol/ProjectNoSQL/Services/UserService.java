package com.lukbol.ProjectNoSQL.Services;

import com.lukbol.ProjectNoSQL.DTOs.*;
import com.lukbol.ProjectNoSQL.Exceptions.ApplicationException;
import com.lukbol.ProjectNoSQL.Models.BlacklistedToken;
import com.lukbol.ProjectNoSQL.Models.Role;
import com.lukbol.ProjectNoSQL.Models.User;
import com.lukbol.ProjectNoSQL.Repositories.*;
import com.lukbol.ProjectNoSQL.Utils.JwtUtil;
import com.lukbol.ProjectNoSQL.Utils.UserUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserUtils userUtils;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ApiResponseDTO authenticateUser(AuthenticateRequestDTO requestDTO) {
        String usernameOrEmail = requestDTO.usernameOrEmail();
        String password = requestDTO.password();
        String username;

        if (usernameOrEmail.contains("@") && usernameOrEmail.contains(".")) {
            User userByEmail = userRepository.findByEmail(usernameOrEmail);
            if (userByEmail == null) {
                throw new ApplicationException.UserNotFoundException(
                        "Nie znaleziono użytkownika o takim adresie email."
                );
            }
            username = userByEmail.getUsername();
        } else {
            username = usernameOrEmail;
        }

        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(
                        "Brak użytkownika z taką nazwą: " + username
                ));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(username);
        return new ApiResponseDTO(true, "Zalogowano pomyślnie. Token: " + token);
    }

    public ApiResponseDTO registerUser(RegisterUserDTO dto) {
        if (userUtils.emailExists(dto.email()))
            throw new ApplicationException.UserWithEmailAlreadyExistsException("Użytkownik o takim adresie email już istnieje.");

        if (userUtils.phoneNumberExists(dto.phoneNumber()))
            throw new ApplicationException.UserWithPhoneNumberAlreadyExistsException("Użytkownik o takim numerze telefonu już istnieje.");

        if (userUtils.usernameExists(dto.username()))
            throw new ApplicationException.UserWithUsernameAlreadyExistsException("Użytkownik o takiej nazwie użytkownika już istnieje.");

        if (!userUtils.isValidPassword(dto.password()))
            throw new ApplicationException.InvalidPasswordException("Hasło musi spełniać określone kryteria bezpieczeństwa.");

        User user = new User(dto.name(), dto.surname(), dto.email(), dto.phoneNumber(),
                passwordEncoder.encode(dto.password()), dto.username(), false);
        Role role = roleRepository.findByName("ROLE_CLIENT");
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user); // jeśli coś pójdzie nie tak, DataAccessException zostanie obsłużone globalnie
        return new ApiResponseDTO(true, "Poprawnie utworzono konto.");
    }

    public UserDTO getUserDetails(Authentication authentication) {
        if (authentication == null)
            throw new ApplicationException.UserNotFoundException("Brak uwierzytelnienia.");

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(
                        "Nie znaleziono użytkownika z nazwą: " + username
                ));

        return new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getSurname(), user.getEmail(), user.getPhoneNumber());
    }

    public ApiResponseDTO changeProfile(Authentication authentication, UpdateProfileDTO dto) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(
                        "Nie znaleziono użytkownika z nazwą: " + username
                ));

        if (userUtils.isNullOrEmpty(dto.name()) || userUtils.isNullOrEmpty(dto.surname())
                || userUtils.isNullOrEmpty(dto.email()) || userUtils.isNullOrEmpty(dto.phoneNumber()))
            throw new ApplicationException.EmptyFieldException("Wszystkie wartości muszą być wypełnione.");

        if (!dto.password().equals(dto.repeatPassword()))
            throw new ApplicationException.PasswordsDoNotMatchException("Hasła nie są takie same.");

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new ApplicationException.InvalidPasswordException("Nowe hasło jest takie samo jak poprzednie.");

        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return new ApiResponseDTO(true, "Poprawnie zapisano zmiany.");
    }

    public ApiResponseDTO deleteUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(
                        "Nie znaleziono użytkownika z nazwą: " + username
                ));

        userRepository.delete(user);
        return new ApiResponseDTO(true, "Poprawnie usunięto konto.");
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getName(), u.getSurname(), u.getEmail(), u.getPhoneNumber()))
                .toList();
    }

    public Optional<UserDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getName(), u.getSurname(), u.getEmail(), u.getPhoneNumber()));
    }

    public ApiResponseDTO logout(HttpServletRequest request) {
        String token = jwtUtil.extractJwtFromRequest(request);
        if (token == null)
            throw new ApplicationException.UserNotFoundException("Nie znaleziono tokenu.");

        Claims claims = jwtUtil.extractAllClaims(token);
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, claims.getIssuedAt());
        blacklistedTokenRepository.save(blacklistedToken);

        return new ApiResponseDTO(true, "Wylogowano pomyślnie");
    }

    public List<UserDTO> getUsersByProjectId(String projectId) {
        return projectRepository.findById(projectId)
                .map(p -> Stream.concat(
                                p.getMembers() != null ? p.getMembers().stream() : Stream.empty(),
                                Stream.ofNullable(p.getOwner())
                        ).map(u -> new UserDTO(u.getId(), u.getUsername(), u.getName(), u.getSurname(), u.getEmail(), u.getPhoneNumber()))
                        .toList())
                .orElse(Collections.emptyList());
    }

    public List<UserDTO> getUsersByTaskId(String taskId) {
        return taskRepository.findById(taskId)
                .map(t -> {
                    if (t.getAssignedTo() != null) {
                        return t.getAssignedTo().stream()
                                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getName(), u.getSurname(), u.getEmail(), u.getPhoneNumber()))
                                .toList();
                    } else {
                        return Collections.<UserDTO>emptyList();
                    }
                })
                .orElseGet(Collections::emptyList);
    }
}
