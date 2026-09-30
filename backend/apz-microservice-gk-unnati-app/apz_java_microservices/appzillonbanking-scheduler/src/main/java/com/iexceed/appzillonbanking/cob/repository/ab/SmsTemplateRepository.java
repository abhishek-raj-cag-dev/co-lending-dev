package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cob.domain.ab.SmsTemplate;
import com.iexceed.appzillonbanking.cob.domain.ab.SmsTemplatePK;

public interface SmsTemplateRepository extends CrudRepository<SmsTemplate, SmsTemplatePK> {

}