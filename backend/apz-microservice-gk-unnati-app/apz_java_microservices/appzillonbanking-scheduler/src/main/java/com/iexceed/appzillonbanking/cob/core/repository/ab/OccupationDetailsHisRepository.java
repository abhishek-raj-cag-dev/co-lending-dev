package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetailsHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OccupationDetailsHisRepository extends CrudRepository<OccupationDetailsHistory, BigDecimal> {

}
