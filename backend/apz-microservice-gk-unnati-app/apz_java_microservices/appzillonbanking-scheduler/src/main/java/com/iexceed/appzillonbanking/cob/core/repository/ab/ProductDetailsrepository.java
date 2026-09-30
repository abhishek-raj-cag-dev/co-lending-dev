package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ProductDetails;

@Repository
public interface ProductDetailsrepository extends CrudRepository<ProductDetails, String> {

	@Query(value ="select productDetails FROM ProductDetails productDetails "
			+ "where productDetails.productGroupCode=:prodGrpCode AND productDetails.productStatus=:productactivestatus AND "
			+ "((productDetails.startDate <= :today AND productDetails.endDate >= :today) "
			+ "OR (productDetails.startDate is null AND productDetails.endDate is null) "
			+ "OR (productDetails.startDate <= :today AND productDetails.endDate is null)) order by productDetails.slNum asc", nativeQuery = false)
	List<ProductDetails> findProductDetails(@Param("prodGrpCode") String prodGrpCode, @Param("productactivestatus") String productactivestatus,  @Param("today") LocalDate today);

	@Query(value ="select productDetails FROM ProductDetails productDetails "
			+ "where productDetails.productStatus=:productactivestatus AND "
			+ "((productDetails.startDate <= :today AND productDetails.endDate >= :today) "
			+ "OR (productDetails.startDate is null AND productDetails.endDate is null) "
			+ "OR (productDetails.startDate <= :today AND productDetails.endDate is null)) order by productDetails.slNum asc", nativeQuery = false)
	List<ProductDetails> findProductDetails(@Param("productactivestatus") String productactivestatus, @Param("today") LocalDate today);
	
	@Query(value ="select productDetails FROM ProductDetails productDetails "
			+ "where productDetails.productCode=:productCode AND productDetails.productStatus=:productactivestatus AND "
			+ "((productDetails.startDate <= :today AND productDetails.endDate >= :today) "
			+ "OR (productDetails.startDate is null AND productDetails.endDate is null) "
			+ "OR (productDetails.startDate <= :today AND productDetails.endDate is null)) order by productDetails.slNum asc", nativeQuery = false)
	Optional<ProductDetails> findProductDetailsBasedOnProductCode(@Param("productCode") String productCode, @Param("productactivestatus") String productactivestatus,  @Param("today") LocalDate today);
}