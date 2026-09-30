package com.iexceed.appzillonbanking.cob.repository.ab;

import java.math.BigDecimal;
import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.CRSDetailsHistory;

public interface CRSDetailsHisRepository extends CrudRepository<CRSDetailsHistory, BigDecimal> {

}
