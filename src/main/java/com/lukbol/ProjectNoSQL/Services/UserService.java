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

import static com.lukbol.ProjectNoSQL.Common.Constants.*;


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

    private static final String USER_ROLE = "ROLE_CLIENT";

    public ApiResponseDTO authenticateUser(AuthenticateRequestDTO requestDTO) {
        String usernameOrEmail = requestDTO.usernameOrEmail();
        String password = requestDTO.password();
        String username;

        if (usernameOrEmail.contains("@") && usernameOrEmail.contains(".")) {
            User userByEmail = userRepository.findByEmail(usernameOrEmail);
            if (userByEmail == null) {
                throw new ApplicationException.UserNotFoundException(USER_NOT_FOUND_BY_EMAIL);
            }
            username = userByEmail.getUsername();
        } else {
            username = usernameOrEmail;
        }

        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(String.format(USER_NOT_FOUND, username)));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(username);
        return new ApiResponseDTO(true, String.format(AUTHENTICATED_SUCCESSFULLY, token));
    }

    public ApiResponseDTO registerUser(RegisterUserDTO dto) {
        if (userUtils.emailExists(dto.email()))
            throw new ApplicationException.UserWithEmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);

        if (userUtils.phoneNumberExists(dto.phoneNumber()))
            throw new ApplicationException.UserWithPhoneNumberAlreadyExistsException(PHONE_ALREADY_EXISTS);

        if (userUtils.usernameExists(dto.username()))
            throw new ApplicationException.UserWithUsernameAlreadyExistsException(USERNAME_ALREADY_EXISTS);

        if (!userUtils.isValidPassword(dto.password()))
            throw new ApplicationException.InvalidPasswordException(INVALID_PASSWORD);

        User user = new User(dto.name(), dto.surname(), dto.email(), dto.phoneNumber(),
                passwordEncoder.encode(dto.password()), dto.username(), false);
        Role role = roleRepository.findByName(USER_ROLE);
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);
        return new ApiResponseDTO(true, ACCOUNT_CREATED);
    }

    public UserDTO getUserDetails(Authentication authentication) {
        if (authentication == null)
            throw new ApplicationException.UserNotFoundException(USER_NOT_AUTHORIZED);

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(String.format(USER_NOT_FOUND, username)));

        return new UserDTO(user.getId(), user.getUsername(), user.getName(), user.getSurname(), user.getEmail(), user.getPhoneNumber());
    }

    public ApiResponseDTO changeProfile(Authentication authentication, UpdateProfileDTO dto) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(String.format(USER_NOT_FOUND, username)));

        if (userUtils.isNullOrEmpty(dto.name()) || userUtils.isNullOrEmpty(dto.surname())
                || userUtils.isNullOrEmpty(dto.email()) || userUtils.isNullOrEmpty(dto.phoneNumber()))
            throw new ApplicationException.EmptyFieldException(EMPTY_FIELDS);

        if (!dto.password().equals(dto.repeatPassword()))
            throw new ApplicationException.PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH);

        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new ApplicationException.InvalidPasswordException(PASSWORD_SAME_AS_OLD);

        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return new ApiResponseDTO(true, PROFILE_UPDATED);
    }

    public ApiResponseDTO deleteUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findOptionalByUsername(username)
                .orElseThrow(() -> new ApplicationException.UserNotFoundException(String.format(USER_NOT_FOUND, username)));

        userRepository.delete(user);
        return new ApiResponseDTO(true, ACCOUNT_DELETED);
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
            throw new ApplicationException.UserNotFoundException(TOKEN_NOT_FOUND);

        Claims claims = jwtUtil.extractAllClaims(token);
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, claims.getIssuedAt());
        blacklistedTokenRepository.save(blacklistedToken);

        return new ApiResponseDTO(true, LOGGED_OUT);
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