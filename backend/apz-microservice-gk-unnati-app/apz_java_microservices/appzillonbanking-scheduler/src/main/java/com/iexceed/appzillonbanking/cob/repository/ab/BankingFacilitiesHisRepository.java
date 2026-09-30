package com.iexceed.appzillonbanking.cob.repository.ab;

import java.math.BigDecimal;
import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.BankingFacilitiesHistory;

public interface BankingFacilitiesHisRepository extends CrudRepository<BankingFacilitiesHistory, BigDecimal> { 

}
