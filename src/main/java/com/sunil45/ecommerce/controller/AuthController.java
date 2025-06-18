package com.sunil45.ecommerce.controller;

import com.sunil45.ecommerce.dto.ErrorResponse;
import com.sunil45.ecommerce.enums.Roles;
import com.sunil45.ecommerce.exceptions.ApiResponse;
import com.sunil45.ecommerce.model.Role;
import com.sunil45.ecommerce.model.User;
import com.sunil45.ecommerce.repository.RoleRepository;
import com.sunil45.ecommerce.repository.UserRepository;
import com.sunil45.ecommerce.security.dto.LoginRequest;
import com.sunil45.ecommerce.security.dto.SignupRequest;
import com.sunil45.ecommerce.security.dto.UserInfoResponse;
import com.sunil45.ecommerce.security.jwt.JwtUtils;
import com.sunil45.ecommerce.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String JWT_COOKIE_HEADER = HttpHeaders.SET_COOKIE;

    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        UserInfoResponse response = new UserInfoResponse(userDetails.getUserId(), userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            UserInfoResponse response = new UserInfoResponse(userDetails.getUserId(), userDetails.getUsername(), roles,
                    jwtCookie.getValue());

            return ResponseEntity.ok().header(JWT_COOKIE_HEADER, jwtCookie.toString()).body(response);
        } catch (AuthenticationException exception) {
            ErrorResponse error = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    "Bad credentials", request.getServletPath(), Instant.now().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(getRole(Roles.ROLE_USER));
        } else {
            strRoles.forEach(role -> {
                switch (role.trim().toLowerCase()) {
                case "admin" -> roles.add(getRole(Roles.ROLE_ADMIN));
                case "seller" -> roles.add(getRole(Roles.ROLE_SELLER));
                default -> roles.add(getRole(Roles.ROLE_USER));
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User registered successfully!"));

    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse("You've been signed out!"));
    }

    private Role getRole(Roles roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found."));
    }
}
