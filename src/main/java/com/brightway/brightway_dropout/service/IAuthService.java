package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.RegisterUserDTO;
import com.brightway.brightway_dropout.dto.requestdtos.SignInDTO;
import com.brightway.brightway_dropout.dto.responsedtos.LoginResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.RegisterUserResponseDTO;

public interface IAuthService {

    LoginResponseDTO signIn(SignInDTO signInDTO);
    RegisterUserResponseDTO registerUser(RegisterUserDTO registerUserDTO);


  }

