package com.esb.appback.controller;

import com.esb.appback.dto.LoginRequest;
import com.esb.appback.dto.LoginResponse;
import com.esb.appback.dto.UserDto;
import com.esb.appback.model.User;
import com.esb.appback.repository.UserRepository;
import com.esb.appback.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor injection (recommended)
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // POST - Login with JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Generate JWT token
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
                // Return user as DTO without password
                UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
                return ResponseEntity.ok(new LoginResponse(true, "Login successful", userDto, token));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "Invalid email or password", null));
    }

    // GET all users
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }

    // GET one user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(u -> ResponseEntity.ok(new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create new user
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        // Hash the password before saving
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);
        UserDto userDto = new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    // PUT - update existing user
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedUser.getName());
                    existing.setEmail(updatedUser.getEmail());
                    existing.setRole(updatedUser.getRole());

                    // Password update - only if provided and not empty
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    User saved = userRepository.save(existing);
                    UserDto userDto = new UserDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
                    return ResponseEntity.ok(userDto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Bonus: simple find by email (very common need)
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(u -> ResponseEntity.ok(new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

