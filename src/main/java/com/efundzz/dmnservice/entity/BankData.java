package com.efundzz.dmnservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bank_data")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;
    private String companyCategory;
    private double roi;
    private double tenure;

}
