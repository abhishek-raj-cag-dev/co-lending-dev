package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationFinancialAnalysis {
	
	@JacksonXmlProperty(localName = "PID")
	private String pid;
	
	@JacksonXmlProperty(localName = "insertionorderid")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "BFA_Monthly_Sales_of_Buss")
    private String bfaMonthlySalesOfBuss;
    
	@JacksonXmlProperty(localName = "BFA_Other_Buss_Income")
    private String bfaOtherBussIncome;
    
	@JacksonXmlProperty(localName = "BFA_Total_Buss_Income")
    private String bfaTotalBussIncome;
    
	@JacksonXmlProperty(localName = "BFA_Cost_of_Sale")
    private String bfaCostOfSale;
    
	@JacksonXmlProperty(localName = "BFA_Rent_Paid")
    private String bfaRentPaid;
    
	@JacksonXmlProperty(localName = "BFA_Sal_Wages_Paid")
    private String bfaSalWagesPaid;
    
	@JacksonXmlProperty(localName = "BFA_Transport_Commu_Expenses")
    private String bfaTransportCommuExpenses;
    
	@JacksonXmlProperty(localName = "BFA_Buss_Loan_Repayments")
    private String bfaBussLoanRepayments;
    
	@JacksonXmlProperty(localName = "BFA_Other_Buss_Expenses")
    private String bfaOtherBussExpenses;
    
	@JacksonXmlProperty(localName = "BFA_Total_Buss_Expenses")
    private String bfaTotalBussExpenses;
    
	@JacksonXmlProperty(localName = "BFA_Net_Buss_Income")
    private String bfaNetBussIncome;
    
	@JacksonXmlProperty(localName = "PFA_Salary_Income")
    private String pfaSalaryIncome;
    
	@JacksonXmlProperty(localName = "PFA_OFMember_Income")
    private String pfaOfMemberIncome;
    
	@JacksonXmlProperty(localName = "PFA_Rental_Income")
    private String pfaRentalIncome;
    
	@JacksonXmlProperty(localName = "PFA_Pension_Income")
    private String pfaPensionIncome;
    
	@JacksonXmlProperty(localName = "PFA_Agri_Income")
    private String pfaAgriIncome;
    
	@JacksonXmlProperty(localName = "PFA_Seasonal_Income")
    private String pfaSeasonalIncome;
    
	@JacksonXmlProperty(localName = "PFA_Other_Income")
    private String pfaOtherIncome;
    
	@JacksonXmlProperty(localName = "PFA_Total_Income")
    private String pfaTotalIncome;
    
	@JacksonXmlProperty(localName = "PFA_Rent_Paid")
    private String pfaRentPaid;
    
	@JacksonXmlProperty(localName = "PFA_Food_Expenses")
    private String pfaFoodExpenses;
    
	@JacksonXmlProperty(localName = "PFA_Transport_And_Commu")
    private String pfaTransportAndCommu;
    
	@JacksonXmlProperty(localName = "PFA_Personal_Loan_Repayments")
    private String pfaPersonalLoanRepayments;
    
	@JacksonXmlProperty(localName = "PFA_Children_Edu_Expenses")
    private String pfaChildrenEduExpenses;
    
	@JacksonXmlProperty(localName = "PFA_Medical_Expenses")
    private String pfaMedicalExpenses;
    
	@JacksonXmlProperty(localName = "PFA_Other_Expenses")
    private String pfaOtherExpenses;
    
	@JacksonXmlProperty(localName = "PFA_Total_HH_Expense")
    private String pfaTotalHhExpense;
    
	@JacksonXmlProperty(localName = "PFA_Net_HH_Income")
    private String pfaNetHhIncome;
    
	@JacksonXmlProperty(localName = "BFA_Utility_Expenses")
    private String bfaUtilityExpenses;
    
	@JacksonXmlProperty(localName = "PFA_Total_Net_Income")
    private String pfaTotalNetIncome;
    
	@JacksonXmlProperty(localName = "PFA_Utility_Paid")
    private String pfaUtilityPaid;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
    private String custNameList;
    
	@JacksonXmlProperty(localName = "fid")
    private String fid;

}
