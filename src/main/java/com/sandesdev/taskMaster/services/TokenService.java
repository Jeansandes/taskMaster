package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.LoginRequest;
import com.sandesdev.taskMaster.dtos.LoginResponse;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private JwtEncoder jwtEncoder;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    public LoginResponse checkUser(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.email());
        if (user.isEmpty() || !user.get().IsLoginCorrect(loginRequest, passwordEncoder)){
            throw new BadCredentialsException("user or password invalid! ");
        }

        var now = Instant.now();
        var expireIn = 300L;

        var scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expireIn))
                .claim("scope", scopes)
                .claim("username", user.get().getName())
                .build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new LoginResponse(jwtValue, expireIn);
    }
}
