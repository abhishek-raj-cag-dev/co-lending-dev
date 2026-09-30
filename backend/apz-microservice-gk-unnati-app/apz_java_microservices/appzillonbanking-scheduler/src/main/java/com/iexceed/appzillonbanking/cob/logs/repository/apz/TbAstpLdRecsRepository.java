package com.iexceed.appzillonbanking.cob.logs.repository.apz;

import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAstpLdRecs;
import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAstpLdRecsPK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAstpLdRecsRepository extends CrudRepository<TbAstpLdRecs, TbAstpLdRecsPK>{

}
