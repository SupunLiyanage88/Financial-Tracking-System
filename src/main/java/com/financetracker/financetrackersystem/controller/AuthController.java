package com.financetracker.financetrackersystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financetracker.financetrackersystem.dto.DeleteUserResponseDTO;
import com.financetracker.financetrackersystem.dto.LoginRequestDTO;
import com.financetracker.financetrackersystem.dto.LoginResponseDTO;
import com.financetracker.financetrackersystem.dto.RegisterRequestDTO;
import com.financetracker.financetrackersystem.dto.RegisterResponseDTO;
import com.financetracker.financetrackersystem.dto.UpdateUserRequestDTO;
import com.financetracker.financetrackersystem.dto.UpdateUserResponseDTO;
import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.service.AuthService;
import com.financetracker.financetrackersystem.service.JWTService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;

    //Get all users by admin
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest req) {
        String userToken = jwtService.extractJwtFromCookie(req);
        try {
            List<UserEntity> users = authService.getAllUsers(userToken);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching users", e);
        }
    }
    
    //Register
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO req) {
        RegisterResponseDTO res = authService.register(req);
        if(res.getError()!=null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginData, HttpServletResponse response) {
        LoginResponseDTO res = authService.login(loginData);
        if(res.getError()!=null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        

        Cookie jwtCookie = new Cookie("jwt", res.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60*60);
        jwtCookie.setDomain("localhost");

        response.addCookie(jwtCookie);


        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //update user
    @PutMapping("/user-update")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(
        @RequestBody UpdateUserRequestDTO req,
        HttpServletRequest request
    ){
        String jwtToken = jwtService.extractJwtFromCookie(request);
        UpdateUserResponseDTO res = authService.updateUser(req, jwtToken);
        if(res.getError()!=null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    
    //delete user by user
    @DeleteMapping("/user-delete/{verify}")
    public ResponseEntity<DeleteUserResponseDTO> deleteUser(
        @PathVariable String verify,  
        HttpServletRequest request    
    ) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        DeleteUserResponseDTO res = authService.deleteUser(verify, jwtToken);

        if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //delete user by admin
    @DeleteMapping("delete-user-by-admin/{userId}/{verify}")
    public ResponseEntity<DeleteUserResponseDTO> deleteUserByAdmin(
        @PathVariable String userId,
        @PathVariable String verify,
        HttpServletRequest request
    ){
        String jwtToken = jwtService.extractJwtFromCookie(request);
        DeleteUserResponseDTO res = authService.deleteUserByAdmin(userId, jwtToken, verify);

        if(res.getError() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);            
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
