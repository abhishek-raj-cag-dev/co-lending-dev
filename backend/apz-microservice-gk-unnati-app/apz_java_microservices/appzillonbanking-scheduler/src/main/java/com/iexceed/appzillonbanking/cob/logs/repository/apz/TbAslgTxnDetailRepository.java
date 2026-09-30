package com.iexceed.appzillonbanking.cob.logs.repository.apz;

import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAslgTxnDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAslgTxnDetailRepository extends CrudRepository<TbAslgTxnDetail,String>{

}
