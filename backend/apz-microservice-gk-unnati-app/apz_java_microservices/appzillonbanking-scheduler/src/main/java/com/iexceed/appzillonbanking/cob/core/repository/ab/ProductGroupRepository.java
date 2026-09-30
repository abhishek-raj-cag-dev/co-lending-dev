package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ProductGroup;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ProductGroupId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductGroupRepository extends CrudRepository<ProductGroup, ProductGroupId> {

	List<ProductGroup> findByProductGroupStatusOrderBySlNumAsc(String string);

}