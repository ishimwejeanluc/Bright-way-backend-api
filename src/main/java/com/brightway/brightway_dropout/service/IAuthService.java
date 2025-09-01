package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.SignInDTO;
import com.brightway.brightway_dropout.dto.responsedtos.LoginResponseDTO;

public interface IAuthService {

    LoginResponseDTO signIn(SignInDTO signInDTO);


  }

