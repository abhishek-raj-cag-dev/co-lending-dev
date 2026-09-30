package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationMasterHisRepository extends CrudRepository<ApplicationMasterHistory, ApplicationMasterId> {

}