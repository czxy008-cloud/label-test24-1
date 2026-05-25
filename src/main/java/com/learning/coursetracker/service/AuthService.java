package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.LoginRequest;
import com.learning.coursetracker.dto.LoginResponse;
import com.learning.coursetracker.dto.RegisterRequest;
import com.learning.coursetracker.entity.User;
import com.learning.coursetracker.mapper.UserMapper;
import com.learning.coursetracker.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        Optional<User> existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        Optional<User> existingEmail = userMapper.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role("STUDENT")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());

        return buildLoginResponse(token, user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());

        return buildLoginResponse(token, user);
    }

    private LoginResponse buildLoginResponse(String token, User user) {
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .build())
                .build();
    }
}
