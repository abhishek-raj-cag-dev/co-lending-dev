package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.SanctionMaster;

@Repository
public interface SanctionMasterRepository extends JpaRepository<SanctionMaster, String> {

	@Query(value = "SELECT * FROM tb_abob_sanction_master WHERE product = :product AND :value BETWEEN min_value AND max_value LIMIT 1", 
		       nativeQuery = true)
	public Optional<SanctionMaster> findByProductAndValueBetween(@Param("product") String product,  @Param("value") BigDecimal value );

    public List<SanctionMaster> findByProduct(String productCode);

}
