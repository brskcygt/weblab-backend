package com.weblab_backend.weblab_backend.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weblab_backend.weblab_backend.dto.UserLoginDto;
import com.weblab_backend.weblab_backend.dto.UserRegisterDto;
import com.weblab_backend.weblab_backend.entity.User;
import com.weblab_backend.weblab_backend.response.LoginResponse;
import com.weblab_backend.weblab_backend.response.RegisterResponse;
import com.weblab_backend.weblab_backend.service.AuthService;
import com.weblab_backend.weblab_backend.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final JWTService jwtService;
  private final AuthService authService;

  public AuthController(JWTService jwtService, AuthService authService) {
    this.jwtService = jwtService;
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@RequestBody UserRegisterDto userRegisterDto) {
    User registeredUser = authService.register(userRegisterDto);
    RegisterResponse registerResponse = new RegisterResponse();
    registerResponse.setEmail(registeredUser.getEmail());
    registerResponse.setFullName(registeredUser.getFullName());
    registerResponse.setUsername(registeredUser.getUsername());
    registerResponse.setMessage("Kullanıcı başarılı bir şekilde yaratıldı.");

    return ResponseEntity.ok(registerResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticate(@RequestBody UserLoginDto userLoginDto) {
    User authenticatedUser = authService.authenticate(userLoginDto);
    String token = jwtService.generateToken(authenticatedUser);
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(token);
    loginResponse.setExpiresIn(jwtService.getExpirationTime());

    return ResponseEntity.ok(loginResponse);
  }

  @GetMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String jwt = authHeader.substring(7);
      jwtService.invalidateToken(jwt);
    }

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok("Logged out successfully");
  }
}
