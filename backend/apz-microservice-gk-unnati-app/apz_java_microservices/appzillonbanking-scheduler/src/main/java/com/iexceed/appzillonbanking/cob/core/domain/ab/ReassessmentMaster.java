package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_abob_ca_reassessment_master")
@Getter
@Setter
@ToString
public class ReassessmentMaster {

	@Id
    @Column(name = "reassessment_id")
    private String reassessmentId;

    @Column(name = "product")
    private String product;

    @Column(name = "criteria")
    private String criteria;

    @Column(name = "reassessment_description")
    private String reassessmentDescription;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "state")
    private String state;

    @Column(name = "bcm")
    private String bcm;

    @Column(name = "acm")
    private String acm;

    @Column(name = "am")
    private String am;

    @Column(name = "active")
    private String active;
	
    @Column(name = "ccm")
	private String ccm;
}
