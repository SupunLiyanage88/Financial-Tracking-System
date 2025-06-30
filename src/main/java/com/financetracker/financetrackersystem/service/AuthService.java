package com.financetracker.financetrackersystem.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.DeleteUserResponseDTO;
import com.financetracker.financetrackersystem.dto.LoginRequestDTO;
import com.financetracker.financetrackersystem.dto.LoginResponseDTO;
import com.financetracker.financetrackersystem.dto.RegisterRequestDTO;
import com.financetracker.financetrackersystem.dto.RegisterResponseDTO;
import com.financetracker.financetrackersystem.dto.UpdateUserRequestDTO;
import com.financetracker.financetrackersystem.dto.UpdateUserResponseDTO;
import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    //Get all users
    public List<UserEntity> getAllUsers(String userToken) {
        String userRole = jwtService.getUserRole(userToken);

        if (!userRole.equals("Admin")) {
            throw new IllegalArgumentException("You are not authorized to access this resource");
        }

        return userRepository.findAll();

    }

    //Create User
    public UserEntity createUser(RegisterRequestDTO userData) {
        UserEntity newUser = new UserEntity();
    
        if (userData.getRole() == null || userData.getRole().isEmpty()) {
            userData.setRole("User");
        }
    
        newUser.setUsername(userData.getUsername());
        newUser.setEmail(userData.getEmail()); 
        newUser.setName(userData.getName()); 
        newUser.setPassword(passwordEncoder.encode(userData.getPassword())); 
        newUser.setRole(capitalizeFirstLetter(userData.getRole()));
    
        return userRepository.save(newUser);
    }

    //Login
    public LoginResponseDTO login(LoginRequestDTO logingData){
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    logingData.getUsername(), 
                    logingData.getPassword()
                    )
            );
        } catch (Exception e) {
            return new LoginResponseDTO(
            null, 
            LocalDateTime.now(), 
            "User not found", 
            "Token not generated"
            );
        }

        var userOptional = userRepository.findByUsername(logingData.getUsername());
        UserEntity user = userOptional.get();

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        
        String token = jwtService.getJWTToken(logingData.getUsername(), claims);

        return new LoginResponseDTO(token, LocalDateTime.now(), null, "Token generated");

    }

    //Register
    public RegisterResponseDTO register(RegisterRequestDTO req) {
        if (isUserEnable(req.getUsername())) return new RegisterResponseDTO(null, "User already exists");
    
        if (isUserEnableByEmail(req.getEmail())) return new RegisterResponseDTO(null, "Email already exists");
    
        var userData = this.createUser(req);
        if (userData.getId() == null) return new RegisterResponseDTO(null, "System Error");
    
        return new RegisterResponseDTO(String.format("User registered at %s", userData.getId()), null);
    }

    //Update User
    public UpdateUserResponseDTO updateUser(UpdateUserRequestDTO req, String userToken) {
        String userId = jwtService.getUserId(userToken);
    
        Optional<UserEntity> userOptional = userRepository.findById(userId);
    
        if (userOptional.isEmpty()) {
            return new UpdateUserResponseDTO("User not found", "User not found");
        }
    
        UserEntity user = userOptional.get();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
        // Validate password before making updates
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return new UpdateUserResponseDTO("Password is incorrect", "Password is incorrect");
        }
    
        // Check for duplicate username
        if (!user.getUsername().equals(req.getUsername()) && isUserEnable(req.getUsername())) {
            return new UpdateUserResponseDTO(null, "Username already exists");
        }
    
        // Check for duplicate email
        if (!user.getEmail().equals(req.getEmail()) && isUserEnableByEmail(req.getEmail())) {
            return new UpdateUserResponseDTO(null, "Email already exists");
        }
    
        // Check for duplicate name
        if (!user.getName().equals(req.getName()) && isUserEnable(req.getName())) {
            return new UpdateUserResponseDTO(null, "Name already exists");
        }
    
        // Update user details
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setName(req.getName());
    
        userRepository.save(user);
    
        return new UpdateUserResponseDTO("User updated", null);
        
    }

    //delete User by user
    public DeleteUserResponseDTO deleteUser(String confirm, String userToken) {
        String username = jwtService.getUsername(userToken);
    
        if (!confirm.equalsIgnoreCase("yes")) {
            return new DeleteUserResponseDTO("Type Yes to delete account", "yes keyword not set");
        }
    
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
    
        if (userOptional.isEmpty()) {
            return new DeleteUserResponseDTO("User not found", "Not found");
        }
    
        userRepository.deleteByUsername(username);
        return new DeleteUserResponseDTO("Delete successful", null);
    }

    //delete user by admin
    public DeleteUserResponseDTO deleteUserByAdmin(
        String userId,
        String userToken,
        String verify
    ){
        String userRole = jwtService.getUserRole(userToken);

        if(!"Admin".equals(userRole)){
            return new DeleteUserResponseDTO("Only the admin has authority", "Unathorized");
        }

        if (!verify.equalsIgnoreCase("yes")){
            return new DeleteUserResponseDTO("Verify by typing yes account ID is:"+userId, "No verification");        
        }

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if(userOptional.isEmpty()){
            return new DeleteUserResponseDTO("User not found", "Not Found");
        }
         userRepository.deleteById(userId);
         return new DeleteUserResponseDTO("Delete successfull", null);

    }

    //Capital the first letter
    public static String capitalizeFirstLetter(String role) {
        if (role == null || role.isEmpty()) {
            return role;
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }

    //Support function for Register
    private Boolean isUserEnable(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    private boolean isUserEnableByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    


}
