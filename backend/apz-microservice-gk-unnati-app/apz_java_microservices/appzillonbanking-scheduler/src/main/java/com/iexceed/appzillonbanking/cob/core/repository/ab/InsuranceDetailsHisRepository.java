package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.iexceed.appzillonbanking.cob.core.domain.ab.InsuranceDetailsHistory;

@Repository
public interface InsuranceDetailsHisRepository extends CrudRepository<InsuranceDetailsHistory, BigDecimal> {

}
