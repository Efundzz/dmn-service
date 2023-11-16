package com.efundzz.dmnservice.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "dmn_evaluation_data")
public class DMNEvaluationData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String creditScore;
	private long takeHomeSalaryMonthly;
	private int age;
	private long experience;
	private String residentType;
	private long loanAmount;
	private int foir;
	private String companyCategory;
	private String salaryCreditType;
	private String ownHouse;
	private int jobStability;
	@Lob
	@Column(columnDefinition = "TEXT")
	@Type(type = "text")
	@JsonRawValue
	private String response;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCreditScore() {
		return creditScore;
	}
	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}
	public long getTakeHomeSalaryMonthly() {
		return takeHomeSalaryMonthly;
	}
	public void setTakeHomeSalaryMonthly(long takeHomeSalaryMonthly) {
		this.takeHomeSalaryMonthly = takeHomeSalaryMonthly;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getExperience() {
		return experience;
	}
	public void setExperience(long experience) {
		this.experience = experience;
	}
	public String getResidentType() {
		return residentType;
	}
	public void setResidentType(String residentType) {
		this.residentType = residentType;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public int getFoir() {
		return foir;
	}
	public void setFoir(int foir) {
		this.foir = foir;
	}
	public String getCompanyCategory() {
		return companyCategory;
	}
	public void setCompanyCategory(String companyCategory) {
		this.companyCategory = companyCategory;
	}
	public String getSalaryCreditType() {
		return salaryCreditType;
	}
	public void setSalaryCreditType(String salaryCreditType) {
		this.salaryCreditType = salaryCreditType;
	}
	public String getOwnHouse() {
		return ownHouse;
	}
	public void setOwnHouse(String ownHouse) {
		this.ownHouse = ownHouse;
	}
	public int getJobStability() {
		return jobStability;
	}
	public void setJobStability(int jobStability) {
		this.jobStability = jobStability;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(List<Map<String, Object>> decisionResult) {
        List<Map<String, String>> list = resultsList(decisionResult);
        this.response = convertListToJson(list);
    }

    private List<Map<String, String>> resultsList(List<Map<String, Object>> decisionResult) {
        return decisionResult.stream()
                .map(result -> Map.of(
                		"entity", (String) result.get("entity"),
                		"roi",String.valueOf(result.get("roi")),
                		"tenure",String.valueOf(result.get("tenure")),
                		"proposedEmi", String.valueOf(result.get("proposedEmi")),
                        "probabilityPercentage", String.valueOf(result.get("probabilityPercentage"))
                        ))
                .toList();
    }

    private String convertListToJson(List<Map<String, String>> list) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }	
}
