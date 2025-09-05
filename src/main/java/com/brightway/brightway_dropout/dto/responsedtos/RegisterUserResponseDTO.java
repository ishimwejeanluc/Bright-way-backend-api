package com.brightway.brightway_dropout.dto.responsedtos;


import com.brightway.brightway_dropout.enumeration.EUserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserResponseDTO {

   private UUID id;
    @Enumerated(EnumType.STRING)
    private EUserRole role;



}
