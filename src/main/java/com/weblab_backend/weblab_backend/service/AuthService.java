package com.weblab_backend.weblab_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.weblab_backend.weblab_backend.dto.UserLoginDto;
import com.weblab_backend.weblab_backend.dto.UserRegisterDto;
import com.weblab_backend.weblab_backend.entity.User;
import com.weblab_backend.weblab_backend.repository.UserRepository;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(UserRegisterDto userInfo) {
    User user = new User();
    user.setFullName(userInfo.getFullName());
    user.setEmail(userInfo.getEmail());
    user.setPassword(passwordEncoder.encode(userInfo.getPassword()));

    return userRepository.save(user);
  }

  public User authenticate(UserLoginDto userInfo) {
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(userInfo.getEmail(), userInfo.getPassword()));

    return userRepository.findByEmail(userInfo.getEmail()).orElseThrow();
  }
}
