package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetailsHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface NomineeDetailsHisRepository extends CrudRepository<NomineeDetailsHistory, BigDecimal> {

}
