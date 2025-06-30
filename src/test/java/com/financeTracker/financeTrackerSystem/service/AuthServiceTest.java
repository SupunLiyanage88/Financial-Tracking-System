package com.financeTracker.financeTrackerSystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import com.financetracker.financetrackersystem.dto.*;
import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.repository.UserRepository;
import com.financetracker.financetrackersystem.service.AuthService;
import com.financetracker.financetrackersystem.service.JWTService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserEntity userEntity;
    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;
    private UpdateUserRequestDTO updateUserRequestDTO;

    @BeforeEach
    public void setUp() {
        userEntity = new UserEntity();
        userEntity.setId("1");
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setName("Test User");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole("User");

        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setUsername("testuser");
        registerRequestDTO.setEmail("test@example.com");
        registerRequestDTO.setName("Test User");
        registerRequestDTO.setPassword("password");
        registerRequestDTO.setRole("User");

        loginRequestDTO = new LoginRequestDTO("testuser", "password" );

        updateUserRequestDTO = new UpdateUserRequestDTO();
        updateUserRequestDTO.setUsername("newuser");
        updateUserRequestDTO.setEmail("new@example.com");
        updateUserRequestDTO.setName("New User");
        updateUserRequestDTO.setPassword("password");
    }

    @Test
    public void testCreateUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity createdUser = authService.createUser(registerRequestDTO);

        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("Test User", createdUser.getName());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("User", createdUser.getRole());

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testLogin_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        when(jwtService.getJWTToken(anyString(), anyMap())).thenReturn("token");

        LoginResponseDTO loginResponse = authService.login(loginRequestDTO);

        assertNotNull(loginResponse);
        assertEquals("token", loginResponse.getToken());
        assertNull(loginResponse.getError());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(jwtService, times(1)).getJWTToken(anyString(), anyMap());
    }

    @Test
    public void testLogin_Failure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("User not found"));

        LoginResponseDTO loginResponse = authService.login(loginRequestDTO);

        assertNotNull(loginResponse);
        assertNull(loginResponse.getToken());
        assertEquals("User not found", loginResponse.getError());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testRegister_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        RegisterResponseDTO registerResponse = authService.register(registerRequestDTO);

        assertNotNull(registerResponse);
        assertNotNull(registerResponse.getMessage());
        assertNull(registerResponse.getError());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        RegisterResponseDTO registerResponse = authService.register(registerRequestDTO);

        assertNotNull(registerResponse);
        assertEquals("User already exists", registerResponse.getError());

        verify(userRepository, times(1)).findByUsername(anyString());
    }


    @Test
    public void testDeleteUser_Success() {
        when(jwtService.getUsername(anyString())).thenReturn("testuser");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        DeleteUserResponseDTO deleteResponse = authService.deleteUser("yes", "token");

        assertNotNull(deleteResponse);
        assertEquals("Delete successful", deleteResponse.getMessage());
        assertNull(deleteResponse.getError());

        verify(jwtService, times(1)).getUsername(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).deleteByUsername(anyString());
    }

    @Test
    public void testDeleteUserByAdmin_Success() {
        when(jwtService.getUserRole(anyString())).thenReturn("Admin");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(userEntity));

        DeleteUserResponseDTO deleteResponse = authService.deleteUserByAdmin("1", "token", "yes");

        assertNotNull(deleteResponse);
        assertEquals("Delete successfull", deleteResponse.getMessage());
        assertNull(deleteResponse.getError());

        verify(jwtService, times(1)).getUserRole(anyString());
        verify(userRepository, times(1)).findById(anyString());
        verify(userRepository, times(1)).deleteById(anyString());
    }
}