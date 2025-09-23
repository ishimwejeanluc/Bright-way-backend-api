package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.user.request.RegisterUserDTO;
import com.brightway.brightway_dropout.dto.user.request.SignInDTO;
import com.brightway.brightway_dropout.dto.user.response.LoginResponseDTO;
import com.brightway.brightway_dropout.dto.user.response.RegisterUserResponseDTO;

public interface IAuthService {

    LoginResponseDTO signIn(SignInDTO signInDTO);
    RegisterUserResponseDTO registerUser(RegisterUserDTO registerUserDTO);


  }

