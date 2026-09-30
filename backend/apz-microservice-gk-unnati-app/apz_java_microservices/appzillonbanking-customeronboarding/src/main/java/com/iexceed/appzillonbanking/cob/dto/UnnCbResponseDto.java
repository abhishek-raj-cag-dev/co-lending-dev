package com.iexceed.appzillonbanking.cob.dto;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnnCbResponseDto {

    @SerializedName("Customer_id")
    private BigDecimal Customer_id;

    @SerializedName("CAGL_DPD_Flag")
    private String CAGL_DPD_Flag;

    @SerializedName("applicant_Indebtedness")
    private String applicant_Indebtedness;

    @SerializedName("Approved_Loan_Amount")
    private BigDecimal Approved_Loan_Amount;

    @SerializedName("GST_pf")
    private Double GST_pf;

    @SerializedName("Insurance_charge_Total")
    private Double Insurance_charge_Total;

    @SerializedName("applicant_Write_Off_Amount")
    private String applicant_Write_Off_Amount;

    @SerializedName("score")
    private String score;

    @SerializedName("FOIR")
    private String FOIR;

    @SerializedName("Eligible_EMI")
    private String Eligible_EMI;

    @SerializedName("applicantType")
    private String applicantType;

    @SerializedName("Final_Decision")
    private String Final_Decision;

    @SerializedName("Installment_amt")
    private Double Installment_amt;

    @SerializedName("app_indebtedness_limit")
    private Double app_indebtedness_limit;

    @SerializedName("coapp_indebtedness_limit")
    private Double coapp_indebtedness_limit;

    @SerializedName("applicant_Writeoff_Suit_filed_Flag")
    private String applicant_Writeoff_Suit_filed_Flag;

    @SerializedName("coapp_max_loan_limit")
    private Double coapp_max_loan_limit;

    @SerializedName("applied_loan_code")
    private String applied_loan_code;

    @SerializedName("co_applicant_Write_Off_Amount")
    private String co_applicant_Write_Off_Amount;

    @SerializedName("IRIS_message")
    private String IRIS_message;

    @SerializedName("applicant_overdue_amount")
    private String applicant_overdue_amount;

    @SerializedName("co_applicant_Overdue_Amount")
    private String co_applicant_Overdue_Amount;

    @SerializedName("CAGL_Unnati_Flag")
    private String CAGL_Unnati_Flag;

    @SerializedName("request_Date")
    private String request_Date;

    @SerializedName("Loan_ID")
    private String Loan_ID;

    @SerializedName("Insurance_charge_Joint")
    private Double Insurance_charge_Joint;

    @SerializedName("Repayment_frequency")
    private String Repayment_frequency;

    @SerializedName("OTS_flag")
    private String OTS_flag;

    @SerializedName("ROI")
    private String ROI;

    @SerializedName("Insurance_charge_Spouse")
    private Double Insurance_charge_Spouse;

    @SerializedName("co_applicant_Writeoff_Suit_filed_Flag")
    private String co_applicant_Writeoff_Suit_filed_Flag;

    @SerializedName("EIR")
    private String EIR;

    @SerializedName("Final_FOIR")
    private Double Final_FOIR;

    @SerializedName("stamp_duty")
    private Double stamp_duty;

    @SerializedName("app_max_loan_limit")
    private Double app_max_loan_limit;

    @SerializedName("Score")
    private String Score;

    @SerializedName("co_applicant_Indebtedness")
    private String co_applicant_Indebtedness;

    @SerializedName("Final_Tenure")
    private String Final_Tenure;

    @SerializedName("Processing_fees_incl_GST")
    private Double Processing_fees_incl_GST;

    @SerializedName("Insurance_charge_Member")
    private Double Insurance_charge_Member;

    @SerializedName("flow_response")
    private String flow_response;

    @SerializedName("Rejection_reason")
    private String Rejection_reason;

    @SerializedName("Processing_fees")
    private Double Processing_fees;

    @SerializedName("Approved_Loan_EMI")
    private Double Approved_Loan_EMI;

    @SerializedName("Indebtedness")
    private String Indebtedness;

    @SerializedName("Final_FOIR_obligation")
    private String Final_FOIR_obligation;

    @SerializedName("vishesh_eligibility")
    private String vishesh_eligibility;

    @SerializedName("vishesh_indebt_eligible_amount")
    private Double vishesh_indebt_eligible_amount;

    @SerializedName("unnati_tentative_eligibility")
    private String unnati_tentative_eligibility;

    @SerializedName("unnati_tentative_eligible_amount")
    private Double unnati_tentative_eligible_amount;

    @SerializedName("unnati_lite_tentative_eligibility")
    private String unnati_lite_tentative_eligibility;

    @SerializedName("unnati_lite_tentative_eligible_amount")
    private Double unnati_lite_tentative_eligible_amount;
    
    
    
    // BigDecimal safe
    public BigDecimal getCustomer_id() {
        return Customer_id != null ? Customer_id : BigDecimal.ZERO;
    }

    public BigDecimal getApproved_Loan_Amount() {
        return Approved_Loan_Amount != null ? Approved_Loan_Amount : BigDecimal.ZERO;
    }

    // Double safe
    public Double getGST_pf() {
        return GST_pf != null ? GST_pf : 0.0;
    }

    public Double getInsurance_charge_Total() {
        return Insurance_charge_Total != null ? Insurance_charge_Total : 0.0;
    }

    public Double getInstallment_amt() {
        return Installment_amt != null ? Installment_amt : 0.0;
    }

    public Double getApp_indebtedness_limit() {
        return app_indebtedness_limit != null ? app_indebtedness_limit : 0.0;
    }

    public Double getCoapp_indebtedness_limit() {
        return coapp_indebtedness_limit != null ? coapp_indebtedness_limit : 0.0;
    }

    public Double getCoapp_max_loan_limit() {
        return coapp_max_loan_limit != null ? coapp_max_loan_limit : 0.0;
    }

    public Double getInsurance_charge_Joint() {
        return Insurance_charge_Joint != null ? Insurance_charge_Joint : 0.0;
    }

    public Double getInsurance_charge_Spouse() {
        return Insurance_charge_Spouse != null ? Insurance_charge_Spouse : 0.0;
    }

    public Double getFinal_FOIR() {
        return Final_FOIR != null ? Final_FOIR : 0.0;
    }

    public Double getStamp_duty() {
        return stamp_duty != null ? stamp_duty : 0.0;
    }

    public Double getApp_max_loan_limit() {
        return app_max_loan_limit != null ? app_max_loan_limit : 0.0;
    }

    public Double getProcessing_fees_incl_GST() {
        return Processing_fees_incl_GST != null ? Processing_fees_incl_GST : 0.0;
    }

    public Double getInsurance_charge_Member() {
        return Insurance_charge_Member != null ? Insurance_charge_Member : 0.0;
    }

    public Double getProcessing_fees() {
        return Processing_fees != null ? Processing_fees : 0.0;
    }

    public Double getApproved_Loan_EMI() {
        return Approved_Loan_EMI != null ? Approved_Loan_EMI : 0.0;
    }

    public Double getVishesh_indebt_eligible_amount() {
        return vishesh_indebt_eligible_amount != null ? vishesh_indebt_eligible_amount : 0.0;
    }

    // String safe
    public String getCAGL_DPD_Flag() {
        return CAGL_DPD_Flag != null ? CAGL_DPD_Flag : "";
    }

    public String getApplicant_Indebtedness() {
        return applicant_Indebtedness != null ? applicant_Indebtedness : "";
    }

    public String getApplicant_Write_Off_Amount() {
        return applicant_Write_Off_Amount != null ? applicant_Write_Off_Amount : "";
    }

    public String getScore() {
        return score != null ? score : "";
    }

    public String getFOIR() {
        return FOIR != null ? FOIR : "";
    }

    public String getEligible_EMI() {
        return Eligible_EMI != null ? Eligible_EMI : "";
    }

    public String getApplicantType() {
        return applicantType != null ? applicantType : "";
    }

    public String getFinal_Decision() {
        return Final_Decision != null ? Final_Decision : "";
    }

    public String getApplied_loan_code() {
        return applied_loan_code != null ? applied_loan_code : "0";
    }

    public String getCo_applicant_Write_Off_Amount() {
        return co_applicant_Write_Off_Amount != null ? co_applicant_Write_Off_Amount : "0";
    }

    public String getIRIS_message() {
        return IRIS_message != null ? IRIS_message : "";
    }

    public String getApplicant_overdue_amount() {
        return applicant_overdue_amount != null ? applicant_overdue_amount : "0";
    }

    public String getCo_applicant_Overdue_Amount() {
        return co_applicant_Overdue_Amount != null ? co_applicant_Overdue_Amount : "0";
    }

    public String getCAGL_Unnati_Flag() {
        return CAGL_Unnati_Flag != null ? CAGL_Unnati_Flag : "";
    }

    public String getRequest_Date() {
        return request_Date != null ? request_Date : "";
    }

    public String getLoan_ID() {
        return Loan_ID != null ? Loan_ID : "0";
    }

    public String getRepayment_frequency() {
        return Repayment_frequency != null ? Repayment_frequency : "";
    }

    public String getOTS_flag() {
        return OTS_flag != null ? OTS_flag : "";
    }

    public String getROI() {
        return ROI != null ? ROI : "0";
    }

    public String getEIR() {
        return EIR != null ? EIR : "0";
    }

    public String getScoreValue() {
        return Score != null ? Score : "0";
    }

    public String getCo_applicant_Indebtedness() {
        return co_applicant_Indebtedness != null ? co_applicant_Indebtedness : "";
    }

    public String getFinal_Tenure() {
        return Final_Tenure != null ? Final_Tenure : "0";
    }

    public String getFlow_response() {
        return flow_response != null ? flow_response : "";
    }

    public String getRejection_reason() {
        return Rejection_reason != null ? Rejection_reason : "";
    }

    public String getIndebtedness() {
        return Indebtedness != null ? Indebtedness : "0";
    }

    public String getFinal_FOIR_obligation() {
        return Final_FOIR_obligation != null ? Final_FOIR_obligation : "0";
    }

    public String getVishesh_eligibility() {
        return vishesh_eligibility != null ? vishesh_eligibility : "0";
    }

    public String getUnnati_tentative_eligibility() {
        return unnati_tentative_eligibility != null ? unnati_tentative_eligibility : "N";
    }

    public Double getUnnati_tentative_eligible_amount() {
        return unnati_tentative_eligible_amount != null ? unnati_tentative_eligible_amount : 0.0;
    }

    public String getUnnati_lite_tentative_eligibility() {
        return unnati_lite_tentative_eligibility != null ? unnati_lite_tentative_eligibility : "N";
    }

    public Double getUnnati_lite_tentative_eligible_amount() {
        return unnati_lite_tentative_eligible_amount != null ? unnati_lite_tentative_eligible_amount : 0.0;
    }
    
    
    
    
    
}