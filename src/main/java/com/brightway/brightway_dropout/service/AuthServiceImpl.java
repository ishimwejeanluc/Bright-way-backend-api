package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.RegisterUserDTO;
import com.brightway.brightway_dropout.dto.requestdtos.SignInDTO;
import com.brightway.brightway_dropout.dto.responsedtos.LoginResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.RegisterUserResponseDTO;
import com.brightway.brightway_dropout.exception.InvalidCredentialsException;
import com.brightway.brightway_dropout.exception.UserAlreadyExistsException;
import com.brightway.brightway_dropout.exception.UserNotFoundException;
import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.repository.IAuthRepository;
import com.brightway.brightway_dropout.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final IAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO signIn(SignInDTO signInDTO) {
        try {

            User found = authRepository.findByEmail(signInDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User with email " + signInDTO.getEmail() + " not found"));

            if (!passwordEncoder.matches(signInDTO.getPassword(), found.getPassword())) {

                throw new InvalidCredentialsException("Invalid password");
            }

            String generatedToken = jwtProvider.generateToken(found);

            return new LoginResponseDTO(generatedToken);
            
        } catch (UserNotFoundException | InvalidCredentialsException e) {

            throw e;
        } catch (Exception e) {

            throw new RuntimeException("Internal server error occurred during sign in", e.getCause());
        }
    }

    @Override
    @Transactional
    public RegisterUserResponseDTO registerUser(RegisterUserDTO registerUserDTO) {
        try {
            // Check if user already exists
            Optional<User> existingUser = authRepository.findByEmail(registerUserDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new UserAlreadyExistsException("User with email " + registerUserDTO.getEmail() + " already exists");
            }

            // Create new user
            User newUser = new User();
            newUser.setName(registerUserDTO.getName());
            newUser.setEmail(registerUserDTO.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
            newUser.setPhone(registerUserDTO.getPhone());
            newUser.setRole(registerUserDTO.getRole());

            // Save user
            User savedUser = authRepository.save(newUser);

            // Return response DTO
            return new RegisterUserResponseDTO(
                savedUser.getId(),
                savedUser.getRole()
            );
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred during user registration", e);
        }
    }

}
