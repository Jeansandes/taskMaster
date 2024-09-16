package com.sandesdev.taskMaster.services;

import com.sandesdev.taskMaster.dtos.LoginRequest;
import com.sandesdev.taskMaster.dtos.LoginResponse;
import com.sandesdev.taskMaster.models.Role;
import com.sandesdev.taskMaster.models.UserModel;
import com.sandesdev.taskMaster.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenServiceTest {
    private static final String EMAIL = "jean@gmail.com";
    private static final String PASSWORD = "123";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "Jean";
    private static final String ROLES = "ROLE_USER ROLE_ADMIN";
    private static final long EXPIRE_IN = 300L;
    @InjectMocks
    private TokenService tokenService;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private LoginRequest loginRequest;
    private UserModel userModel;
    private JwtClaimsSet claims;
    private JwtEncoderParameters jwtEncoderParameters;
    private Jwt jwt;
    @BeforeEach
    void setUp() {
        openMocks(this);
        loginRequest = new LoginRequest(EMAIL, PASSWORD);
        Role userRole = new Role(1L, "ROLE_USER");
        Role adminRole = new Role(2L, "ROLE_ADMIN");

        userModel = new UserModel(USER_ID, USERNAME, EMAIL, PASSWORD);
        userModel.setRoles(Set.of(userRole, adminRole));

        claims = JwtClaimsSet.builder()
                .subject(USER_ID.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRE_IN))
                .claim("scope", ROLES)
                .claim("username", USERNAME)
                .build();

        jwt = Jwt.withTokenValue("mockToken")
                .header("alg", "HS256")
                .claims(claimsMap -> claimsMap.putAll(claims.getClaims()))
                .build();
    }

    @Test
    void whenCheckUserWithValidCredentialsThenReturnLoginResponse() {
        // Mocka o repositório para retornar o usuário
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userModel));

        // Mocka o passwordEncoder para retornar true, indicando que a senha está correta
        when(passwordEncoder.matches(PASSWORD, userModel.getPassword())).thenReturn(true);

        // Mocka o JwtEncoder para retornar o token JWT simulado
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Executa o método a ser testado
        LoginResponse response = tokenService.checkUser(loginRequest);

        // Verifica o resultado
        assertNotNull(response);
        assertEquals("mockToken", response.accessToken());
        assertEquals(EXPIRE_IN, response.inxpiresIn());

        // Verifica que o repositório e o encoder foram chamados
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(passwordEncoder, times(1)).matches(PASSWORD, userModel.getPassword());
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
    @Test
    void whenCheckUserWithInvalidEmailThenThrowBadCredentialsException() {
        // Mocka o repositório para retornar Optional vazio, indicando que o email não foi encontrado
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Executa o método a ser testado e espera uma exceção
        assertThrows(BadCredentialsException.class, () -> tokenService.checkUser(loginRequest));

        // Verifica que o JwtEncoder não foi chamado
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
    }

}