package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BCMPIIncomeDetailsWrapper {
    @JsonProperty("business")
    private Business business;

    @JsonProperty("wage")
    private Wage wage;

    @JsonProperty("agriculture")
    private Agriculture agriculture;

    @JsonProperty("salary")
    private Salary salary;

    @JsonProperty("pension")
    private Pension pension;

    @JsonProperty("rentalIncome")
    private RentalIncome rentalIncome;

    @JsonProperty("homeMaker")
    private String homeMaker;

    @JsonProperty("applicantTotalIncome")
    private BigDecimal applicantTotalIncome;

    @JsonProperty("coApplicantTotalIncome")
    private BigDecimal coApplicantTotalIncome;

    @JsonProperty("otherFamilyIncome")
    private BigDecimal otherFamilyIncome;

    @JsonProperty("fieldAssessedIncome")
    private BigDecimal fieldAssessedIncome;

    @JsonProperty("fieldAssessedIncomeDate")
    private LocalDateTime fieldAssessedIncomeDate;

    @JsonProperty("totalDeclaredIncome")
    private BigDecimal totalDeclaredIncome;

    @JsonProperty("otherIncome")
    private BigDecimal otherIncome;

    private BigDecimal totalBusinessSelfDeclaredIncome;
    private BigDecimal totalWageSelfDeclaredIncome;
    private BigDecimal totalAgricultureSelfDeclaredIncome;
    private BigDecimal totalSalarySelfDeclaredIncome;
    private BigDecimal totalPensionSelfDeclaredIncome;
    private BigDecimal totalRentalSelfDeclaredIncome;

    public static BigDecimal calculateOtherIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal businessIncome = calculateBusinessIncome(incomeDetails.getBusiness(), Constants.APPLICANT).add(
                calculateBusinessIncome(incomeDetails.getBusiness(), Constants.COAPPLICANT_STRING));
        BigDecimal salaryIncome = calculateSalaryIncome(incomeDetails.getSalary(), Constants.APPLICANT).add(
                calculateSalaryIncome(incomeDetails.getSalary(), Constants.COAPPLICANT_STRING));
        BigDecimal wageIncome = calculateWageIncome(incomeDetails.getWage(), Constants.APPLICANT).add(
                calculateWageIncome(incomeDetails.getWage(), Constants.COAPPLICANT_STRING));
        BigDecimal agricultureIncome = calculateAgricultureIncome(incomeDetails.getAgriculture(), Constants.APPLICANT).add(
                calculateAgricultureIncome(incomeDetails.getAgriculture(), Constants.COAPPLICANT_STRING));
        BigDecimal pensionIncome = calculatePensionIncome(incomeDetails.getPension(), Constants.APPLICANT).add(
                calculatePensionIncome(incomeDetails.getPension(), Constants.COAPPLICANT_STRING));
        BigDecimal rentalIncome = calculateRentalIncome(incomeDetails.getRentalIncome(), Constants.APPLICANT).add(
                calculateRentalIncome(incomeDetails.getRentalIncome(), Constants.COAPPLICANT_STRING));

        BigDecimal mainIncome = businessIncome.add(salaryIncome).add(wageIncome);
        BigDecimal sideIncome = agricultureIncome.add(pensionIncome).add(rentalIncome);
        BigDecimal calculatedMainIncome =
                mainIncome.multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(50));

        BigDecimal otherIncome = calculatedMainIncome.min(sideIncome);

        return otherIncome;
    }

    public static BigDecimal calculateTotalBusinessDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }

        if (incomeDetails.getBusiness() != null) {
            if (incomeDetails.getBusiness().getDairy() != null && !incomeDetails.getBusiness().getDairy().isEmpty()) {
                for (Dairy dairy : incomeDetails.getBusiness().getDairy()) {
                    if (dairy != null && dairy.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(dairy.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getTailoring() != null && !incomeDetails.getBusiness().getTailoring().isEmpty()) {
                for (Tailoring tailoring : incomeDetails.getBusiness().getTailoring()) {
                    if (tailoring != null && tailoring.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(tailoring.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getKirana() != null && !incomeDetails.getBusiness().getKirana().isEmpty()) {
                for (Kirana kirana : incomeDetails.getBusiness().getKirana()) {
                    if (kirana != null && kirana.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(kirana.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getOther() != null && !incomeDetails.getBusiness().getOther().isEmpty()) {
                for (OtherBusiness otherBusiness : incomeDetails.getBusiness().getOther()) {
                    if (otherBusiness != null && otherBusiness.getGrossIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(otherBusiness.getNetBusinessIncome());
                    }
                }
            }
        }
        return totalDeclaredIncome;
    }

    public static BigDecimal calculateTotalWageDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }

        if (incomeDetails.getWage() != null &&
                incomeDetails.getWage().getApplicant() != null) {
            if (incomeDetails.getWage().getApplicant().getConstructions() != null &&
                    incomeDetails.getWage().getApplicant().getConstructions().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getConstructions().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getContractLabour() != null &&
                    incomeDetails.getWage().getApplicant().getContractLabour().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getContractLabour().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getMaid() != null &&
                    incomeDetails.getWage().getApplicant().getMaid().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getMaid().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getOther() != null &&
                    incomeDetails.getWage().getApplicant().getOther().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getOther().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getHotelWorker() != null &&
                    incomeDetails.getWage().getApplicant().getHotelWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getHotelWorker().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getFarmWorker() != null &&
                    incomeDetails.getWage().getApplicant().getFarmWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getFarmWorker().getSelfDeclaredWage()));
            }
        }

        if (incomeDetails.getWage() != null &&
                incomeDetails.getWage().getCoApplicant() != null) {
            if (incomeDetails.getWage().getCoApplicant().getConstructions() != null &&
                    incomeDetails.getWage().getCoApplicant().getConstructions().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getConstructions().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getContractLabour() != null &&
                    incomeDetails.getWage().getCoApplicant().getContractLabour().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getContractLabour().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getMaid() != null &&
                    incomeDetails.getWage().getCoApplicant().getMaid().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getMaid().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getOther() != null &&
                    incomeDetails.getWage().getCoApplicant().getOther().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getOther().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getHotelWorker() != null &&
                    incomeDetails.getWage().getCoApplicant().getHotelWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getHotelWorker().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getFarmWorker() != null &&
                    incomeDetails.getWage().getCoApplicant().getFarmWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getFarmWorker().getSelfDeclaredWage()));
            }
        }
        return totalDeclaredIncome;
    }

    public static BigDecimal calculateTotalAgricultureDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }
        if (incomeDetails.getAgriculture() != null) {
            if (incomeDetails.getAgriculture().getApplicant() != null &&
                    incomeDetails.getAgriculture().getApplicant().getDeclaredIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getAgriculture().getApplicant().getDeclaredIncome()));
            }
            if (incomeDetails.getAgriculture().getCoApplicant() != null &&
                    incomeDetails.getAgriculture().getCoApplicant().getDeclaredIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getAgriculture().getCoApplicant().getDeclaredIncome()));
            }
        }
        return totalDeclaredIncome;
    }

    public static BigDecimal calculateTotalSalaryDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }
        if (incomeDetails.getSalary() != null) {
            if (incomeDetails.getSalary().getApplicant() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getSalary().getApplicant().getNetSalary()));
            }
            if (incomeDetails.getSalary().getCoApplicant() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getSalary().getCoApplicant().getNetSalary()));
            }
        }
        return totalDeclaredIncome;
    }

    public static BigDecimal calculateTotalPensionDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }

        if (incomeDetails.getPension() != null) {
            if (incomeDetails.getPension().getApplicant() != null &&
                    incomeDetails.getPension().getApplicant().getApplicantPensionIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getPension().getApplicant().getApplicantPensionIncome()));
            }
            if (incomeDetails.getPension().getCoApplicant() != null &&
                    incomeDetails.getPension().getCoApplicant().getCoApplicantPensionIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getPension().getCoApplicant().getCoApplicantPensionIncome()));
            }
        }
        return totalDeclaredIncome;
    }
    public static BigDecimal calculateTotalRentalDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }
        if (incomeDetails.getRentalIncome() != null) {
            if (incomeDetails.getRentalIncome().getApplicant() != null &&
                    incomeDetails.getRentalIncome().getApplicant().getMonthlyRentalIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getRentalIncome().getApplicant().getMonthlyRentalIncome()));
            }
            if (incomeDetails.getRentalIncome().getCoApplicant() != null &&
                    incomeDetails.getRentalIncome().getCoApplicant().getMonthlyRentalIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getRentalIncome().getCoApplicant().getMonthlyRentalIncome()));
            }
        }

        return totalDeclaredIncome;
    }

    public static BigDecimal calculateTotalDeclaredIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        BigDecimal totalDeclaredIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return totalDeclaredIncome;
        }

        if (incomeDetails.getBusiness() != null) {
            if (incomeDetails.getBusiness().getDairy() != null && !incomeDetails.getBusiness().getDairy().isEmpty()) {
                for (Dairy dairy : incomeDetails.getBusiness().getDairy()) {
                    if (dairy != null && dairy.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(dairy.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getTailoring() != null && !incomeDetails.getBusiness().getTailoring().isEmpty()) {
                for (Tailoring tailoring : incomeDetails.getBusiness().getTailoring()) {
                    if (tailoring != null && tailoring.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(tailoring.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getKirana() != null && !incomeDetails.getBusiness().getKirana().isEmpty()) {
                for (Kirana kirana : incomeDetails.getBusiness().getKirana()) {
                    if (kirana != null && kirana.getNetMonthlyIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(kirana.getNetMonthlyIncome()));
                    }
                }
            }

            if (incomeDetails.getBusiness().getOther() != null && !incomeDetails.getBusiness().getOther().isEmpty()) {
                for (OtherBusiness otherBusiness : incomeDetails.getBusiness().getOther()) {
                    if (otherBusiness != null && otherBusiness.getGrossIncome() != null) {
                        totalDeclaredIncome = totalDeclaredIncome.add(otherBusiness.getNetBusinessIncome());
                    }
                }
            }
        }

        if (incomeDetails.getWage() != null &&
                incomeDetails.getWage().getApplicant() != null) {
            if (incomeDetails.getWage().getApplicant().getConstructions() != null &&
                    incomeDetails.getWage().getApplicant().getConstructions().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getConstructions().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getContractLabour() != null &&
                    incomeDetails.getWage().getApplicant().getContractLabour().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getContractLabour().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getMaid() != null &&
                    incomeDetails.getWage().getApplicant().getMaid().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getMaid().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getOther() != null &&
                    incomeDetails.getWage().getApplicant().getOther().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getOther().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getHotelWorker() != null &&
                    incomeDetails.getWage().getApplicant().getHotelWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getHotelWorker().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getApplicant().getFarmWorker() != null &&
                    incomeDetails.getWage().getApplicant().getFarmWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getApplicant().getFarmWorker().getSelfDeclaredWage()));
            }
        }

        if (incomeDetails.getWage() != null &&
                incomeDetails.getWage().getCoApplicant() != null) {
            if (incomeDetails.getWage().getCoApplicant().getConstructions() != null &&
                    incomeDetails.getWage().getCoApplicant().getConstructions().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getConstructions().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getContractLabour() != null &&
                    incomeDetails.getWage().getCoApplicant().getContractLabour().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getContractLabour().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getMaid() != null &&
                    incomeDetails.getWage().getCoApplicant().getMaid().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getMaid().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getOther() != null &&
                    incomeDetails.getWage().getCoApplicant().getOther().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getOther().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getHotelWorker() != null &&
                    incomeDetails.getWage().getCoApplicant().getHotelWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getHotelWorker().getSelfDeclaredWage()));
            }
            if (incomeDetails.getWage().getCoApplicant().getFarmWorker() != null &&
                    incomeDetails.getWage().getCoApplicant().getFarmWorker().getSelfDeclaredWage() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getWage().getCoApplicant().getFarmWorker().getSelfDeclaredWage()));
            }
        }

        if (incomeDetails.getAgriculture() != null) {
            if (incomeDetails.getAgriculture().getApplicant() != null &&
                    incomeDetails.getAgriculture().getApplicant().getDeclaredIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getAgriculture().getApplicant().getDeclaredIncome()));
            }
            if (incomeDetails.getAgriculture().getCoApplicant() != null &&
                    incomeDetails.getAgriculture().getCoApplicant().getDeclaredIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getAgriculture().getCoApplicant().getDeclaredIncome()));
            }
        }

        if (incomeDetails.getSalary() != null) {
            if (incomeDetails.getSalary().getApplicant() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getSalary().getApplicant().getNetSalary()));
            }
            if (incomeDetails.getSalary().getCoApplicant() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getSalary().getCoApplicant().getNetSalary()));
            }
        }

        if (incomeDetails.getPension() != null) {
            if (incomeDetails.getPension().getApplicant() != null &&
                    incomeDetails.getPension().getApplicant().getApplicantPensionIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getPension().getApplicant().getApplicantPensionIncome()));
            }
            if (incomeDetails.getPension().getCoApplicant() != null &&
                    incomeDetails.getPension().getCoApplicant().getCoApplicantPensionIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getPension().getCoApplicant().getCoApplicantPensionIncome()));
            }
        }

        if (incomeDetails.getRentalIncome() != null) {
            if (incomeDetails.getRentalIncome().getApplicant() != null &&
                    incomeDetails.getRentalIncome().getApplicant().getMonthlyRentalIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getRentalIncome().getApplicant().getMonthlyRentalIncome()));
            }
            if (incomeDetails.getRentalIncome().getCoApplicant() != null &&
                    incomeDetails.getRentalIncome().getCoApplicant().getMonthlyRentalIncome() != null) {
                totalDeclaredIncome = totalDeclaredIncome.add(new BigDecimal(incomeDetails.getRentalIncome().getCoApplicant().getMonthlyRentalIncome()));
            }
        }

        return totalDeclaredIncome;
    }


    public static BigDecimal calculateTotalIncome(BCMPIIncomeDetailsWrapper incomeDetails, String applicantType) {
        BigDecimal applicantTotalIncome = BigDecimal.ZERO;
        if (incomeDetails == null) {
            return applicantTotalIncome;
        }

        if (incomeDetails != null) {
            applicantTotalIncome = applicantTotalIncome
                    .add(calculateBusinessIncome(incomeDetails.getBusiness(), applicantType));
            applicantTotalIncome = applicantTotalIncome
                    .add(calculateWageIncome(incomeDetails.getWage(), applicantType));
            applicantTotalIncome = applicantTotalIncome
                    .add(calculateAgricultureIncome(incomeDetails.getAgriculture(), applicantType));
            applicantTotalIncome = applicantTotalIncome
                    .add(calculateSalaryIncome(incomeDetails.getSalary(), applicantType));
            applicantTotalIncome = applicantTotalIncome
                    .add(calculatePensionIncome(incomeDetails.getPension(), applicantType));
            applicantTotalIncome = applicantTotalIncome
                    .add(calculateRentalIncome(incomeDetails.getRentalIncome(), applicantType));
        }

        return applicantTotalIncome;
    }

    public static BigDecimal calculateBusinessIncome(Business business, String applicantType) {
        BigDecimal businessIncome = BigDecimal.ZERO;
        if (business == null) {
            return businessIncome;
        }
        if (business != null) {
            if (business.getDairy() != null) {
                businessIncome = businessIncome.add(calculateDairyIncome(business.getDairy(), applicantType));
            }
            if (business.getTailoring() != null) {
                businessIncome = businessIncome.add(calculateTailoringIncome(business.getTailoring(), applicantType));
            }
            if (business.getKirana() != null) {
                businessIncome = businessIncome.add(calculateKiranaIncome(business.getKirana(), applicantType));
            }
            if (business.getOther() != null) {
                businessIncome = businessIncome.add(calculateOtherBusinessIncome(business.getOther(), applicantType));
            }
        }
        return businessIncome;
    }

    private static BigDecimal calculateDairyIncome(List<Dairy> dairyList, String applicantType) {
        BigDecimal dairyIncome = BigDecimal.ZERO;
        if (dairyList == null || dairyList.isEmpty()) {
            return dairyIncome;
        }
        if (dairyList == null || dairyList.isEmpty()) {
            return dairyIncome;
        }

        for (Dairy dairy : dairyList) {
            BigDecimal income = dairy.getIncomeAssessmentChecknetBusinessIncome();
            String dairyType = dairy.getDairyType();

            if (income != null && dairyType != null) {
                if (dairyType.equalsIgnoreCase(applicantType)) {
                    dairyIncome = dairyIncome.add(income);
                }
            }
        }

        return dairyIncome;
    }

    private static BigDecimal calculateTailoringIncome(List<Tailoring> tailoringList, String applicantType) {
        BigDecimal tailoringIncome = BigDecimal.ZERO;
        if (tailoringList == null || tailoringList.isEmpty()) {
            return tailoringIncome;
        }
        if (tailoringList != null && !tailoringList.isEmpty()) {
            for (Tailoring tailoring : tailoringList) {
                if (tailoring.getNetBusinessIncome() != null) {
                    if (tailoring.getTailoringType().equalsIgnoreCase(applicantType)) {
                        tailoringIncome = tailoringIncome.add(tailoring.getNetBusinessIncome());
                    }
                }
            }
        }
        return tailoringIncome;
    }

    private static BigDecimal calculateKiranaIncome(List<Kirana> kiranaList, String applicantType) {
        BigDecimal kiranaIncome = BigDecimal.ZERO;
        if (kiranaList == null || kiranaList.isEmpty()) {
            return kiranaIncome;
        }
        if (kiranaList != null && !kiranaList.isEmpty()) {
            for (Kirana kirana : kiranaList) {
                if (null != kirana.getFinalNetIncome()) {
                    if (kirana.getKiranaType().equalsIgnoreCase(applicantType)) {
                        kiranaIncome = kiranaIncome.add(kirana.getFinalNetIncome());
                    }
                }
            }
        }
        return kiranaIncome;
    }

    private static BigDecimal calculateOtherBusinessIncome(List<OtherBusiness> otherBusinessList,
            String applicantType) {
        BigDecimal otherBusinessIncome = BigDecimal.ZERO;
        if (otherBusinessList == null || otherBusinessList.isEmpty()) {
            return otherBusinessIncome;
        }
        if (otherBusinessList != null && !otherBusinessList.isEmpty()) {
            for (OtherBusiness otherBusiness : otherBusinessList) {
                if (otherBusiness.getNetBusinessIncome() != null) {
                    if (otherBusiness.getOtherType().equalsIgnoreCase(applicantType)) {
                        otherBusinessIncome = otherBusinessIncome.add(otherBusiness.getNetBusinessIncome());
                    }
                }
            }
        }
        return otherBusinessIncome;
    }

    public static BigDecimal calculateWageIncome(Wage wage, String applicantType) {
        if (wage == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal wageIncome = BigDecimal.ZERO;

        if (Constants.APPLICANT.equalsIgnoreCase(applicantType)) {
            if(null != wage.getApplicant()) {
                wageIncome = wageIncome.add(sumWageDetails(wage.getApplicant()));
            }
        } else if (Constants.CO_APPLICANT.equalsIgnoreCase(applicantType)) {
            if(null != wage.getCoApplicant()) {
                wageIncome = wageIncome.add(sumWageDetails(wage.getCoApplicant()));
            }
        }
        return wageIncome;
    }

    private static BigDecimal sumWageDetails(WageDetail wageDetail) {
        if (wageDetail == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        total = total.add(calculateMonthlyWageIncome(wageDetail.getFarmWorker()));
        total = total.add(calculateMonthlyWageIncome(wageDetail.getHotelWorker()));
        total = total.add(calculateMonthlyWageIncome(wageDetail.getOther()));
        total = total.add(calculateMonthlyWageIncome(wageDetail.getContractLabour()));
        total = total.add(calculateMonthlyWageIncome(wageDetail.getConstructions()));
        total = total.add(calculateMonthlyWageIncome(wageDetail.getMaid()));

        return total;
    }


    public static BigDecimal calculateMonthlyWageIncome(JobDetail jobDetail) {
        if (jobDetail != null && jobDetail.getTotalWagePerMonth() != null) {
            return new BigDecimal(jobDetail.getTotalWagePerMonth());
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal calculateAgricultureIncome(Agriculture agriculture, String applicantType) {
        BigDecimal agricultureIncome = BigDecimal.ZERO;
        if (agriculture == null) {
            return agricultureIncome;
        } //use considered income
        if (Constants.APPLICANT.equalsIgnoreCase(applicantType)) {
            if (agriculture.getApplicant() != null) {
                AgricultureDetail agricultureDetail = agriculture.getApplicant();
                if (agricultureDetail.getConsideredIncome() != null) {
                    agricultureIncome = agricultureIncome
                            .add(new BigDecimal(agricultureDetail.getConsideredIncome()));
                }
            }
        } else if (Constants.CO_APPLICANT.equalsIgnoreCase(applicantType)) {
            if (agriculture.getCoApplicant() != null) {
                AgricultureDetail agricultureDetail = agriculture.getCoApplicant();
                if (agricultureDetail.getConsideredIncome() != null) {
                    agricultureIncome = agricultureIncome
                            .add(new BigDecimal(agricultureDetail.getConsideredIncome()));
                }
            }
        }
        return agricultureIncome;
    }

    private static BigDecimal calculateSalaryIncome(Salary salary, String applicantType) {
        BigDecimal salaryIncome = BigDecimal.ZERO;
        if (salary == null) {
            return salaryIncome;
        }

        if (Constants.APPLICANT.equalsIgnoreCase(applicantType)) { //use net salary
            SalaryDetails applicantSalary = salary.getApplicant();
            if (applicantSalary != null && applicantSalary.getNetSalary() != null) {
                salaryIncome = salaryIncome
                        .add(new BigDecimal(applicantSalary.getNetSalary()));
            }
        } else if (Constants.CO_APPLICANT.equalsIgnoreCase(applicantType)) {
            SalaryDetails coApplicantSalary = salary.getCoApplicant();
            if (coApplicantSalary != null && coApplicantSalary.getNetSalary() != null) {
                salaryIncome = salaryIncome
                        .add(new BigDecimal(coApplicantSalary.getNetSalary()));
            }
        }

        return salaryIncome;
    }

    private static BigDecimal calculatePensionIncome(Pension pension, String applicantType) {
        BigDecimal pensionIncome = BigDecimal.ZERO;
        if (pension == null) {
            return pensionIncome;
        }
        if (pension != null) {
            if (Constants.APPLICANT.equalsIgnoreCase(applicantType)) {
                if (pension.getApplicant() != null && pension.getApplicant().getApplicantPensionIncome() != null) {
                    pensionIncome = pensionIncome
                            .add(new BigDecimal(pension.getApplicant().getApplicantPensionIncome()));
                }
            } else if (Constants.CO_APPLICANT.equalsIgnoreCase(applicantType)) {
                if (pension.getCoApplicant() != null
                        && pension.getCoApplicant().getCoApplicantPensionIncome() != null) {
                    pensionIncome = pensionIncome
                            .add(new BigDecimal(pension.getCoApplicant().getCoApplicantPensionIncome()));
                }
            }
        }
        return pensionIncome;
    }

    private static BigDecimal calculateRentalIncome(RentalIncome rentalIncome, String applicantType) {
        BigDecimal rentalIncomeNum = BigDecimal.ZERO;
        if (rentalIncome == null) {
            return rentalIncomeNum;
        }

        if (Constants.APPLICANT.equalsIgnoreCase(applicantType)) {//use considered monthly income
            RentalDetail rentalDetail = rentalIncome.getApplicant();
            if (rentalDetail != null && rentalDetail.getConsideredMonthlyIncome() != null) {
                rentalIncomeNum = rentalIncomeNum
                        .add(rentalDetail.getConsideredMonthlyIncome());
            }
        } else if (Constants.CO_APPLICANT.equalsIgnoreCase(applicantType)) {
            RentalDetail rentalDetail = rentalIncome.getCoApplicant();
            if (rentalDetail != null && rentalDetail.getConsideredMonthlyIncome() != null) {
                rentalIncomeNum = rentalIncomeNum
                        .add(rentalDetail.getConsideredMonthlyIncome());
            }
        }
        return rentalIncomeNum;
    }

    public static BigDecimal calculateFieldAssessedIncome(BCMPIIncomeDetailsWrapper incomeDetails) {
        return incomeDetails.getApplicantTotalIncome()
                .add(incomeDetails.getCoApplicantTotalIncome())
                .add(incomeDetails.getOtherFamilyIncome());
    }


    @Data
    public static class Business {
        @JsonProperty("dairy")
        private List<Dairy> dairy;

        @JsonProperty("Tailoring")
        private List<Tailoring> Tailoring;

        @JsonProperty("Kirana")
        private List<Kirana> Kirana;

        @JsonProperty("Other")
        private List<OtherBusiness> Other;
    }

    @Data
    public static class Dairy {
        @JsonProperty("dairyLocation")
        private String dairyLocation;

        @JsonProperty("dairyTenure")
        private String dairyTenure;

        @JsonProperty("dairyBusinessType")
        private String dairyBusinessType;

        @JsonProperty("cowDetails")
        private List<cowAndBuffaloDetail> cowDetails;

        @JsonProperty("buffaloDetails")
        private List<cowAndBuffaloDetail> buffaloDetails;

        @JsonProperty("cowBreed1")
        private String cowBreed1;

        @JsonProperty("cowBreed2")
        private String cowBreed2;

        @JsonProperty("cowBreed3")
        private String cowBreed3;

        @JsonProperty("cowBreed4")
        private String cowBreed4;

        @JsonProperty("cowBreed5")
        private String cowBreed5;

        @JsonProperty("noOfCowBreed1")
        private BigDecimal noOfCowBreed1;

        @JsonProperty("noOfCowBreed2")
        private BigDecimal noOfCowBreed2;

        @JsonProperty("noOfCowBreed3")
        private BigDecimal noOfCowBreed3;

        @JsonProperty("noOfCowBreed4")
        private BigDecimal noOfCowBreed4;

        @JsonProperty("noOfCowBreed5")
        private BigDecimal noOfCowBreed5;

        @JsonProperty("buffaloBreed1")
        private String buffaloBreed1;

        @JsonProperty("buffaloBreed2")
        private String buffaloBreed2;

        @JsonProperty("buffaloBreed3")
        private String buffaloBreed3;

        @JsonProperty("buffaloBreed4")
        private String buffaloBreed4;

        @JsonProperty("buffaloBreed5")
        private String buffaloBreed5;

        @JsonProperty("noOfBuffaloBreed1")
        private BigDecimal noOfBuffaloBreed1;

        @JsonProperty("noOfBuffaloBreed2")
        private BigDecimal noOfBuffaloBreed2;

        @JsonProperty("noOfBuffaloBreed3")
        private BigDecimal noOfBuffaloBreed3;

        @JsonProperty("noOfBuffaloBreed4")
        private BigDecimal noOfBuffaloBreed4;

        @JsonProperty("noOfBuffaloBreed5")
        private BigDecimal noOfBuffaloBreed5;


        @JsonProperty("netMonthlyIncome")
        private String netMonthlyIncome;

        @JsonProperty("organizationName")
        private String organizationName;

        @JsonProperty("businessPremiseOwnership")
        private String businessPremiseOwnership;

        @JsonProperty("businessAddressSameAsResidance")
        private String businessAddressSameAsResidance;

        @JsonProperty("totalBuffalos")
        private BigDecimal totalBuffalos;

        @JsonProperty("totalCows")
        private BigDecimal totalCows;

        @JsonProperty("totalCowsAndBuffalos")
        private BigDecimal totalCowsAndBuffalos;

        @JsonProperty("avgDailyRevenuePerBuffalo")
        private BigDecimal avgDailyRevenuePerBuffalo;

        @JsonProperty("avgMonthlyRevenuePerBuffalo")
        private BigDecimal avgMonthlyRevenuePerBuffalo;

        @JsonProperty("avgDailyRevenuePerCow")
        private BigDecimal avgDailyRevenuePerCow;

        @JsonProperty("avgMonthlyRevenuePerCow")
        private BigDecimal avgMonthlyRevenuePerCow;

        @JsonProperty("IncomeAssessmentChecknetBusinessIncome")
        private BigDecimal IncomeAssessmentChecknetBusinessIncome;

        @JsonProperty("DairyType")
        private String DairyType;
    }

    @Data
    public static class cowAndBuffaloDetail{
        @JsonProperty("breed")
        private String breed;

        @JsonProperty("count")
        private BigDecimal count;
    }


    @Data
    public static class Tailoring {

        @JsonProperty("tailorShopItems")
        private List<String> tailorShopItems;

        @JsonProperty("tailoringBusinessLocation")
        private String tailoringBusinessLocation;

        @JsonProperty("tailoringTenure")
        private String tailoringTenure;

        @JsonProperty("tailoringPremise")
        private String tailoringPremise;

        @JsonProperty("numOfTailoringMachines")
        private String numOfTailoringMachines;

        @JsonProperty("tailoringWorkType")
        private String tailoringWorkType;

        @JsonProperty("tailoringMachineType")
        private String tailoringMachineType;

        @JsonProperty("tailoringBusinessType")
        private String tailoringBusinessType;

        @JsonProperty("numOfTailors")
        private String numOfTailors;

        @JsonProperty("netMonthlyIncome")
        private String netMonthlyIncome;

        @JsonProperty("organizationName")
        private String organizationName;

        @JsonProperty("businessAddressSameAsResidance")
        private String businessAddressSameAsResidance;

        @JsonProperty("revenuePerDay")
        private BigDecimal revenuePerDay;

        @JsonProperty("revenuePerMonth")
        private BigDecimal revenuePerMonth;

        @JsonProperty("totalMonthlyIncome")
        private BigDecimal totalMonthlyIncome;

        @JsonProperty("netBusinessIncome")
        private BigDecimal netBusinessIncome;

        @JsonProperty("tailoringType")
        private String tailoringType;

        // Added fields below
        @JsonProperty("numOfPowerTailoringMachine")
        private String numOfPowerTailoringMachine;

        @JsonProperty("numOfManualTailoringMachine")
        private String numOfManualTailoringMachine;

        @JsonProperty("numOfSalariedTailors")
        private String numOfSalariedTailors;

        @JsonProperty("stitchingFor")
        private String stitchingFor;

        @JsonProperty("isApplicantATailor")
        private String isApplicantATailor;

        @JsonProperty("isCoApplicantPartOfTailorBusiness")
        private String isCoApplicantPartOfTailorBusiness;
    }


    @Data
    public static class Kirana {
        @JsonProperty("kiranaBusinessLocation")
        private String kiranaBusinessLocation;

        @JsonProperty("kiranaTenure")
        private String kiranaTenure;

        @JsonProperty("kiranaShopType")
        private String kiranaShopType;

        @JsonProperty("kiranaMarketClassification")
        private String kiranaMarketClassification;

        @JsonProperty("kiranaUnit")
        private String kiranaUnit;

        @JsonProperty("kiranaGodownUnit")
        private String kiranaGodownUnit;

        @JsonProperty("kiranaOccupancyLevel")
        private String kiranaOccupancyLevel;

        @JsonProperty("kiranaBusinessType")
        private String kiranaBusinessType;

        @JsonProperty("areaofshop")
        private BigDecimal areaofshop;

        @JsonProperty("areaOfGodown")
        private BigDecimal areaOfGodown;

        @JsonProperty("netMonthlyIncome")
        private String netMonthlyIncome;

        @JsonProperty("nameOfOrganization")
        private String nameOfOrganization;

        @JsonProperty("businessAddressSameAsResidance")
        private String businessAddressSameAsResidance;

        @JsonProperty("areaOfTheShopInSqft")
        private BigDecimal areaOfTheShopInSqft;

        @JsonProperty("areaOfTheGodownInSqft")
        private BigDecimal areaOfTheGodownInSqft;

        @JsonProperty("totalArea")
        private BigDecimal totalArea;

        @JsonProperty("inventoryArea")
        private BigDecimal inventoryArea;

        @JsonProperty("inventoryValue")
        private BigDecimal inventoryValue;

        @JsonProperty("monthlySales")
        private BigDecimal monthlySales;

        @JsonProperty("calculatedNetIncome")
        private BigDecimal calculatedNetIncome;

        @JsonProperty("finalNetIncome")
        private BigDecimal finalNetIncome;

        @JsonProperty("kiranaType")
        private String kiranaType;
    }

    @Data
    public static class OtherBusiness {
        @JsonProperty("otherBusinessTenure")
        private String otherBusinessTenure;

        @JsonProperty("otherBusinessType")
        private String otherBusinessType;

        @JsonProperty("lineOfOtherBusiness")
        private String lineOfOtherBusiness;

        @JsonProperty("nameOfOrganization")
        private String nameOfOrganization;

        @JsonProperty("salesPerDay")
        private String salesPerDay;

        @JsonProperty("numberOfOperatingDays")
        private String numberOfOperatingDays;

        @JsonProperty("operatingExpense")
        private String operatingExpense;

        @JsonProperty("rentOrLease")
        private String rentOrLease;

        @JsonProperty("numberOfEmployees")
        private String numberOfEmployees;

        @JsonProperty("salaryPaidPerMonth")
        private String salaryPaidPerMonth;

        @JsonProperty("transportAndCommunication")
        private String transportAndCommunication;

        @JsonProperty("waterBill")
        private String waterBill;

        @JsonProperty("electricityBill")
        private String electricityBill;

        @JsonProperty("gasBill")
        private String gasBill;

        @JsonProperty("telephoneBill")
        private String telephoneBill;

        @JsonProperty("otherBills")
        private String otherBills;

        @JsonProperty("otherBusinessExpense")
        private String otherBusinessExpense;

        @JsonProperty("monthlySales")
        private BigDecimal monthlySales;

        @JsonProperty("grossIncome")
        private BigDecimal grossIncome;

        @JsonProperty("totalSalary")
        private BigDecimal totalSalary;

        @JsonProperty("totalUtilities")
        private BigDecimal totalUtilities;

        @JsonProperty("totalBusinessExpenses")
        private BigDecimal totalBusinessExpenses;

        @JsonProperty("netBusinessIncome")
        private BigDecimal netBusinessIncome;

        @JsonProperty("businessAddressSameAsResidance")
        private String businessAddressSameAsResidance;

        @JsonProperty("otherType")
        private String otherType;
    }

    @Data
    public static class Wage {
        @JsonProperty("applicant")
        private WageDetail applicant;

        @JsonProperty("coApplicant")
        private WageDetail coApplicant;
    }

    @Data
    public static class WageDetail {
        @JsonProperty("farmWorker")
        private JobDetail farmWorker;

        @JsonProperty("hotelWorker")
        private JobDetail hotelWorker;

        @JsonProperty("other")
        private JobDetail other;

        @JsonProperty("contractLabour")
        private JobDetail contractLabour;

        @JsonProperty("constructions")
        private JobDetail constructions;

        @JsonProperty("maid")
        private JobDetail maid;
    }

    @Data
    public static class JobDetail {
        @JsonProperty("noOfDaysInMonth")
        private String noOfDaysInMonth;

        @JsonProperty("wageEarnedPerDay")
        private String wageEarnedPerDay;

        @JsonProperty("totalWagePerMonth")
        private String totalWagePerMonth;

        @JsonProperty("selfDeclaredWage")
        private String selfDeclaredWage;

        @JsonProperty("otherAct")
        private String otherAct;
    }

    @Data
    public static class Agriculture {
        @JsonProperty("applicant")
        private AgricultureDetail applicant;

        @JsonProperty("coApplicant")
        private AgricultureDetail coApplicant;
    }

    @Data
    public static class AgricultureDetail {
        @JsonProperty("typeOfAgriculture")
        private String typeOfAgriculture;

        @JsonProperty("cropType")
        private String cropType;

        @JsonProperty("crop")
        private String crop;

        @JsonProperty("land")
        private String land;

        @JsonProperty("landInAcres")
        private String landInAcres;

        @JsonProperty("maxYeildPerAcre")
        private String maxYeildPerAcre;

        @JsonProperty("monthlyIncome")
        private String monthlyIncome;

        @JsonProperty("consideredIncome")
        private String consideredIncome;

        @JsonProperty("declaredIncome")
        private String declaredIncome;
    }

    @Data
    public static class Salary {
        @JsonProperty("applicant")
        private SalaryDetails applicant;

        @JsonProperty("coApplicant")
        private SalaryDetails coApplicant;
    }

    @Data
    public static class SalaryDetails {
        @JsonProperty("workPlace")
        private String workPlace;

        @JsonProperty("monthTenure")
        private String monthTenure;

        @JsonProperty("yearTenure")
        private String yearTenure;

        @JsonProperty("natureOfEmployment")
        private String natureOfEmployment;

        @JsonProperty("grossSalary")
        private String grossSalary;

        @JsonProperty("netSalary")
        private String netSalary;

        @JsonProperty("modeOfSalary")
        private String modeOfSalary;
    }

    @Data
    public static class Pension {
        @JsonProperty("applicant")
        private ApplicantPension applicant;

        @JsonProperty("coApplicant")
        private CoApplicantPension coApplicant;
    }

    @Data
    public static class ApplicantPension {
        @JsonProperty("applicantPensionIncome")
        private String applicantPensionIncome;
    }

    @Data
    public static class CoApplicantPension {
        @JsonProperty("coApplicantPensionIncome")
        private String coApplicantPensionIncome;
    }

    @Data
    public static class RentalIncome {
        @JsonProperty("applicant")
        private RentalDetail applicant;

        @JsonProperty("coApplicant")
        private RentalDetail coApplicant;
    }

    @Data
    public static class RentalDetail {
        @JsonProperty("monthlyRentalIncome")
        private String monthlyRentalIncome;

        @JsonProperty("buildingType")
        private String buildingType;

        @JsonProperty("consideredMonthlyIncome")
        private BigDecimal consideredMonthlyIncome;
    }
}
