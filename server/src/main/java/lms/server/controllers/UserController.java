package lms.server.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lms.server.domain.Result;
import lms.server.domain.UserService;
import lms.server.models.User;
import lms.server.models.dtos.LoginRequest;
import lms.server.models.dtos.RegisterRequest;
import lms.server.models.dtos.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public UserController(
            UserService userService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(List.of("Not authenticated."));
        }

        String normalizedEmail = authentication.getName().toLowerCase().trim();

        Optional<User> user = userService.findByEmailWithRoles(normalizedEmail);

        if (user.isEmpty()) {
            return ResponseEntity.status(401).body(List.of("User not found."));
        }

        return ResponseEntity.ok(new UserResponse(user.get()));
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterRequest request) {
        Result<User> result = userService.registerStudent(request);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        return ResponseEntity.ok(new UserResponse(result.getPayload()));
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody RegisterRequest request) {
        Result<User> result = userService.registerTeacher(request);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        return ResponseEntity.ok(new UserResponse(result.getPayload()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        if (request == null || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(List.of("Email and password are required."));
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            normalizedEmail,
                            request.getPassword()
                    )
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            Optional<User> user = userService.findByEmailWithRoles(normalizedEmail);

            if (user.isEmpty()) {
                return ResponseEntity.status(401).body(List.of("Invalid email or password."));
            }

            return ResponseEntity.ok(new UserResponse(user.get()));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(List.of("Invalid email or password."));
        }
    }
}
