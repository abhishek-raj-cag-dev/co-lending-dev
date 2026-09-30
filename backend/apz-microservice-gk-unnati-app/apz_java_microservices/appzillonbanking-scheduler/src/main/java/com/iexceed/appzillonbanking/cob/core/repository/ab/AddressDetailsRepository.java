package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressDetailsRepository extends CrudRepository<AddressDetails, BigDecimal> {

	public List<AddressDetails> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,
			int versionNum);

	public List<AddressDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	public void deleteByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId, BigDecimal custDtlId);

	public List<AddressDetails> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId,
			int versionNum, BigDecimal custDtlId);

	public Optional<AddressDetails> findByApplicationIdAndAppIdAndVersionNumAndUniqueId(String applicationId,
			String appId, int oldVersionNum, BigDecimal nomineeDtlsId);

	@Query(value = "SELECT new AddressDetails(a.addressDtlsId) " 
            + "FROM AddressDetails a "
            + "LEFT OUTER JOIN CustomerDetails c ON a.applicationId = c.applicationId "
            + "AND a.custDtlId = c.custDtlId "
            + "WHERE c.customerType = :customerType "
            + "AND a.addressType = :addressType "
            + "AND c.applicationId = :applicationId ", nativeQuery = false)
	Optional<AddressDetails> findAddressByCustomerTypeAndAddressTypeAndApplicationId(@Param("customerType") String customerType, 
             
                                                              @Param("addressType") String addressType,@Param("applicationId") String applicationId);

	@Query(value = "SELECT new AddressDetails(a.addressDtlsId, a.applicationId, a.versionNum, a.appId, a.custDtlId, a.payloadColumn, a.addressType) " 
	        + "FROM AddressDetails a "
	        + "LEFT OUTER JOIN CustomerDetails c ON a.applicationId = c.applicationId "
	        + "AND a.custDtlId = c.custDtlId "
	        + "WHERE c.customerType = :customerType "
	        + "AND a.addressType = :addressType "
	        + "AND c.applicationId = :applicationId ", nativeQuery = false)
	Optional<AddressDetails> findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(@Param("customerType") String customerType, 
	                                                          @Param("addressType") String addressType,@Param("applicationId") String applicationId);


}
