package com.iexceed.appzillonbanking.cob.core.repository.ab;
import com.iexceed.appzillonbanking.cob.core.domain.ab.RpcStageVerification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpcStageVerificationRepository extends CrudRepository<RpcStageVerification, String> {

}