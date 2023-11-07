package com.efundzz.dmnservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.efundzz.dmnservice.entity.BankData;

public interface BankDataRepository extends JpaRepository<BankData, Long> {
    Optional<BankData> findByBankNameAndCompanyCategory(String bankName, String companyCategory);
}
