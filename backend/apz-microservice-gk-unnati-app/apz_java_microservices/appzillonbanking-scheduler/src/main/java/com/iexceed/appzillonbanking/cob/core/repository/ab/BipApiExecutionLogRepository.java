package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.BipApiExecutionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BipApiExecutionLogRepository extends CrudRepository<BipApiExecutionLog, Long> {


}
