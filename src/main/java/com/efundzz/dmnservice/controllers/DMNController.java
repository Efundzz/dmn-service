package com.efundzz.dmnservice.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import com.efundzz.dmnservice.entity.DMNEvaluationData;
import com.efundzz.dmnservice.exception.ValidationException;
import com.efundzz.dmnservice.repository.DMNEvaluationDataRepository;
import com.efundzz.dmnservice.services.DMNService;

@RestController
@CrossOrigin
public class DMNController {
    @Autowired
    private DMNService dmnService;
    @Autowired
    private DMNEvaluationDataRepository dmnDataRepository;
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateDecision(@RequestBody CRMBreFormRequestDTO requestDTO) {
        try {
            System.out.println("Input Variables: " + requestDTO);
            // Assuming the third parameter DMNEvaluationDTO is not required
            List<Map<String,Object>> decisionResult = dmnService.evaluateDecision( requestDTO);
            System.out.println("Decision Result: " + decisionResult);
            if (decisionResult != null && decisionResult.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else if (decisionResult != null) {
                return ResponseEntity.ok(decisionResult);
            }
        } catch (ValidationException ex) {
            // If validation fails return a structured error response
            Map<String, String> fieldErrors = ex.getFieldErrors();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldErrors);
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return null;
    }

    @GetMapping("/evaluation-data")
    public ResponseEntity<List<DMNEvaluationData>> getEvaluationData() {
        List<DMNEvaluationData> evaluationDataList = dmnDataRepository.findAll();
        return ResponseEntity.ok(evaluationDataList);
    }
}
