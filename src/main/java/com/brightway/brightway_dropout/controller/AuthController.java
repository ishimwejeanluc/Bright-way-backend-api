package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.user.request.RegisterUserDTO;
import com.brightway.brightway_dropout.dto.user.request.SignInDTO;
import com.brightway.brightway_dropout.dto.user.response.LoginResponseDTO;
import com.brightway.brightway_dropout.dto.user.response.RegisterUserResponseDTO;
import com.brightway.brightway_dropout.service.IAuthService;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService authService;


    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid  SignInDTO dto) {
        LoginResponseDTO loginResponseDTO = authService.signIn(dto);
        return new ResponseEntity<>(new ApiResponse(
                true,
                "Login successfully",
                loginResponseDTO
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterUserDTO dto) {
        RegisterUserResponseDTO registerUserResponseDTO = authService.registerUser(dto);
        return new ResponseEntity<>(new ApiResponse(
                true,
                "Registration successful",
                registerUserResponseDTO
        ), HttpStatus.CREATED);
    }


}
