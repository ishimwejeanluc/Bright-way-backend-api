package com.brightway.brightway_dropout.controller;


import com.brightway.brightway_dropout.dto.behaviorIncident.request.RegisterBehaviorIncidentDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.RegisterBehaviorIncidentResponseDTO;
import com.brightway.brightway_dropout.service.BehaviorIncidentServiceImpl;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/behavior-incidents")
@RequiredArgsConstructor
public class BehaviorIncidentController {

    private final BehaviorIncidentServiceImpl behaviorIncidentService;

    @PostMapping
    public ResponseEntity<ApiResponse> saveIncident(@RequestBody RegisterBehaviorIncidentDTO registerBehaviordto) {
        RegisterBehaviorIncidentResponseDTO response = behaviorIncidentService.saveIncident(registerBehaviordto);
        return new ResponseEntity<>(
                new ApiResponse(true, "Behavior incident saved successfully", response),
                HttpStatus.CREATED
        );
    }
}
