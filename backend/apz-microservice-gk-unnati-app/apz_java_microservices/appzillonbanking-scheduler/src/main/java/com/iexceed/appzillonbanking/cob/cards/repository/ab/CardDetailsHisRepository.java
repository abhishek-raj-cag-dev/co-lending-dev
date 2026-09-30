package com.iexceed.appzillonbanking.cob.cards.repository.ab;


import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetailsHistory;

import java.math.BigDecimal;


public interface CardDetailsHisRepository extends CrudRepository<CardDetailsHistory, BigDecimal> {
	
	
}