package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.requestdtos.RegisterUserDTO;
import com.brightway.brightway_dropout.dto.requestdtos.SignInDTO;
import com.brightway.brightway_dropout.dto.responsedtos.LoginResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.RegisterUserResponseDTO;
import com.brightway.brightway_dropout.service.AuthServiceImpl;
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
    private final AuthServiceImpl authService;


    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid  SignInDTO dto) {
        LoginResponseDTO loginResponseDTO = authService.signIn(dto);
        return new ResponseEntity<>(new ApiResponse(
                true,
                "Login successfully",
                loginResponseDTO
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterUserDTO dto) {
        RegisterUserResponseDTO registerUserResponseDTO = authService.registerUser(dto);
        return new ResponseEntity<>(new ApiResponse(
                true,
                "Registration successful",
                registerUserResponseDTO
        ), HttpStatus.CREATED);
    }


}
