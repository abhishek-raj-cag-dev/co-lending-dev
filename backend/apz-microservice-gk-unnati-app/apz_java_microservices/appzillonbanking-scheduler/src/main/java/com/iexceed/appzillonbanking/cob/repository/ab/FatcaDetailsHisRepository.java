package com.iexceed.appzillonbanking.cob.repository.ab;

import java.math.BigDecimal;
import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.FatcaDetailsHistory;

public interface FatcaDetailsHisRepository extends CrudRepository<FatcaDetailsHistory, BigDecimal> {

}
