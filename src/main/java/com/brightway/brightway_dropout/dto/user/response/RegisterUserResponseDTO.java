package com.brightway.brightway_dropout.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserResponseDTO {
    private UUID id;
}
