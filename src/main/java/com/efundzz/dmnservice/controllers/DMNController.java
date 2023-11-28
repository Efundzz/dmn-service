package com.efundzz.dmnservice.controllers;

import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import com.efundzz.dmnservice.entity.DMNEvaluationData;
import com.efundzz.dmnservice.exception.ValidationException;
import com.efundzz.dmnservice.repository.DMNEvaluationDataRepository;
import com.efundzz.dmnservice.services.DMNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping(path = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class DMNController {
    @Autowired
    private DMNService dmnService;
    @Autowired
    private DMNEvaluationDataRepository dmnDataRepository;
    @PostMapping("/bre/evaluation")
    public ResponseEntity<?> evaluateDecision(@RequestBody CRMBreFormRequestDTO requestDTO) {
        try {
            List<Map<String,Object>> decisionResult = dmnService.evaluateDecision( requestDTO);
            if (decisionResult != null && decisionResult.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else if (decisionResult != null) {
                return ResponseEntity.ok(decisionResult);
            }
        } catch (ValidationException ex) {
            Map<String, String> fieldErrors = ex.getFieldErrors();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldErrors);
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return null;
    }


    @GetMapping("/bre/getAllEvaluationData")
    public ResponseEntity<List<DMNEvaluationData>> getEvaluationData() {
        List<DMNEvaluationData> evaluationDataList = dmnDataRepository.findAll();
        return ResponseEntity.ok(evaluationDataList);
    }

}
