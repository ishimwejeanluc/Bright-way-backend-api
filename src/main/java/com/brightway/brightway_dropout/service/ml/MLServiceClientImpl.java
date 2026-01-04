package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.PredictionRequestDTO;
import com.brightway.brightway_dropout.dto.ml.PredictionResponseDTO;
import com.brightway.brightway_dropout.exception.MLServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MLServiceClientImpl implements IMLServiceClient {
    
    @Value("${ml.service.url:http://localhost:5000}")
    private String mlServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public MLServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PredictionResponseDTO predictBatch(PredictionRequestDTO request) {
        String url = mlServiceUrl + "/api/predict/batch";
        
        try {
            log.info("Calling ML service at {} with {} predictions", url, request.getPredictions().size());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<PredictionRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<PredictionResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PredictionResponseDTO.class
            );
            
            if (response.getBody() == null) {
                throw new MLServiceException("ML service returned empty response");
            }
            
            log.info("ML service processed {} predictions in {}ms",
                    response.getBody().getProcessedCount(),
                    response.getBody().getProcessingTimeMs());
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Failed to call ML service: {}", e.getMessage());
            throw new MLServiceException("ML service unavailable: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean checkHealth() {
        String url = mlServiceUrl + "/health";
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            boolean isHealthy = response.getStatusCode().is2xxSuccessful();
            
            if (isHealthy) {
                log.debug("ML service is healthy");
            } else {
                log.warn("ML service returned unhealthy status: {}", response.getStatusCode());
            }
            
            return isHealthy;
            
        } catch (RestClientException e) {
            log.error("ML service health check failed: {}", e.getMessage());
            return false;
        }
    }
}
