package com.efundzz.dmnservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecisionResultDTO {
	private String entity;
    private double proposedEmi;
    private double roi;
    private double tenure;

	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public double getProposedEmi() {
		return proposedEmi;
	}
	public void setProposedEmi(double proposedEmi) {
		this.proposedEmi = proposedEmi;
	}
	public double getRoi() {
		return roi;
	}
	public void setRoi(double roi) {
		this.roi = roi;
	}
	public double getTenure() {
		return tenure;
	}
	public void setTenure(double tenure) {
		this.tenure = tenure;
	}


	public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", this.entity);
        map.put("proposed emi", this.proposedEmi);
        map.put("roi", this.roi);
        map.put("tenure", this.tenure);
        return map;
    }
}
