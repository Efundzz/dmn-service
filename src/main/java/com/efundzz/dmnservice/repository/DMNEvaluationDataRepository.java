package com.efundzz.dmnservice.repository;

import com.efundzz.dmnservice.entity.DMNEvaluationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DMNEvaluationDataRepository extends JpaRepository<DMNEvaluationData, Long> {

}
