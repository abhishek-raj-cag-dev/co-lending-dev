package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.RPCMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RPCMasterRepository extends JpaRepository<RPCMaster, RPCMaster.TbAbobRpcMasterId> {
}
