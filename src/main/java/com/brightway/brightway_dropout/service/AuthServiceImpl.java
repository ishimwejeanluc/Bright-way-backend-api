package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.SignInDTO;
import com.brightway.brightway_dropout.dto.responsedtos.LoginResponseDTO;
import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.repository.IAuthRepository;
import com.brightway.brightway_dropout.security.JwtProvider;
import com.brightway.brightway_dropout.security.SHA256PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final IAuthRepository authRepository;
    private final SHA256PasswordEncoder passwordEncoder = new SHA256PasswordEncoder();
    private final JwtProvider jwtProvider = new JwtProvider();


    @Override
    public LoginResponseDTO signIn(SignInDTO signInDTO) {
        User found = authRepository.findByEmail(signInDTO.getEmail())
                .orElseThrow(() -> new ObjectNotFoundException(User.class, "User with this Email not found"));

        if (Objects.nonNull(found)) {
            String hashedPassword = passwordEncoder.encode(signInDTO.getPassword());
            if (passwordEncoder.matches(found.getPassword(), hashedPassword)) {
                String generatedToken = jwtProvider.generateToken(found);
                return new LoginResponseDTO(generatedToken);
            }

        }
        return null;
    }
}
