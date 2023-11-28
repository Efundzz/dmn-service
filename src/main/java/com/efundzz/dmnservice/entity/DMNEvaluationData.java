package com.efundzz.dmnservice.entity;


import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "dmn_evaluation_data")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DMNEvaluationData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;
	@Column(name = "brand")
	private String brand;
	@Column(name = "agent_id")
	private String agentId;
	@Column(name = "credit_score")
	private String creditScore;
	@Column(name = "monthly_salary")
	private long takeHomeSalaryMonthly;
	@Column(name = "age")
	private int age;
	@Column(name = "experience")
	private long experience;
	@Column(name = "resident_type")
	private String residentType;
	@Column(name = "loan_amount")
	private long loanAmount;
	@Column(name = "foir")
	private int foir;
	@Column(name = "company_category")
	private String companyCategory;
	@Column(name = "salary_credit_type")
	private String salaryCreditType;
	@Column(name = "own_house_anywhere")
	private String ownHouse;
	@Column(name = "current_job_stability")
	private int jobStability;
	@Type(type = "jsonb")
	@Column(columnDefinition = "TEXT")
	private List<Map<String,Object>> response;

}
