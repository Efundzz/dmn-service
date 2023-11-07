package com.efundzz.dmnservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bank_data")
public class BankData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;
    private String companyCategory;
    private double roi;
    private double tenure;
    public BankData() {
        super();
    }
    public BankData(Long id, String bankName, String companyCategory, double roi, double tenure) {
        super();
        this.id = id;
        this.bankName = bankName;
        this.companyCategory = companyCategory;
        this.roi = roi;
        this.tenure = tenure;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getCompanyCategory() {
        return companyCategory;
    }
    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
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
    @Override
    public String toString() {
        return "BankData [id=" + id + ", bankName=" + bankName + ", companyCategory=" + companyCategory + ", roi=" + roi
                + ", tenure=" + tenure + "]";
    }


}
