package com.iexceed.appzillonbanking.cob.loans.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BranchWageDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BranchWageDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;
import com.iexceed.appzillonbanking.cob.loans.payload.BuffaloesListRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.CowsListRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.IncomeCalculatorRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.IncomeCalculatorRequestFields;
import com.iexceed.appzillonbanking.cob.repository.ab.LovMasterRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class IncomeAssessmentService {

	private static final Logger logger = LogManager.getLogger(IncomeAssessmentService.class);

	@Autowired
	private LovMasterRepository lovMasterRepo;

	@Autowired
	private BranchWageDetailsRepository branchWageDetailsRepo;

	@CircuitBreaker(name = "fallback", fallbackMethod = "incomeAssessmentCheckFallback")
	public Response incomeAssessmentCheck(IncomeCalculatorRequest apiRequest) {
		JSONObject resp = new JSONObject();
		Response incomeAssessmentCheckResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		incomeAssessmentCheckResponse.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		try {
			JSONObject locObj = new JSONObject();
			IncomeCalculatorRequestFields req = apiRequest.getRequestObj();
			LovMaster lovDetails = null;
			JSONObject lovContent = new JSONObject();
			if (!"WAGE".equalsIgnoreCase(apiRequest.getIncomeType().toUpperCase())) {
				lovDetails = lovMasterRepo.findByLovName(apiRequest.getIncomeType().toUpperCase());
				lovContent = new JSONObject(lovDetails.getLovDtls());
			}
			switch (apiRequest.getIncomeType().toUpperCase()) {
                case "DAIRY":
                    String breedOfCow;
                    int noOfCows;
                    String breedOfBuffaloes;
                    int noOfBuffaloes;
                    int monthlyDairyBusinessNetIncome = 0;
                    LovMaster dairyStateLov = lovMasterRepo.findByLovName(Constants.MILK_RATES_PER_STATE);
                    JSONObject dairyStateLovContent = new JSONObject(dairyStateLov.getLovDtls());
                    String dairyState = req.getDairyRequestFields()
                            .getDairyState()
                            .replaceAll("\\s+", "").toUpperCase();
                    logger.debug("dairyState : {}", dairyState);
                    JSONArray statesArray = dairyStateLovContent.getJSONArray("states");
                    JSONObject selectedStateObj = null;

                    for (int i = 0; i < statesArray.length(); i++) {
                        JSONObject stateObj = statesArray.getJSONObject(i);
                        if (stateObj.getString("name").equalsIgnoreCase(dairyState)) {
                            selectedStateObj = stateObj;
                            break;
                        }
                    }
                    if (selectedStateObj == null) {
                        throw new Exception("State not found in LOV");
                    }
                    JSONObject ratesObj = selectedStateObj.getJSONObject("rates");
                    int cowMilkRate = ratesObj.getInt("cow");
                    int buffaloMilkRate = ratesObj.getInt("buffalo");

                    logger.debug("selectedStateObj : {}", selectedStateObj.toString());
                    String dairyBusinessLocation = req.getDairyRequestFields().getBusinessLocation().trim();
                    locObj = lovContent.getJSONObject("Business_Location").getJSONObject(dairyBusinessLocation);
                    double nonMilkingPeriod = Double.parseDouble(lovContent.getString("nonMilkingPeriod"));
                    logger.debug("nonMilkingPeriod : {}", nonMilkingPeriod);
                    List<CowsListRequestFields> cowList = req.getDairyRequestFields().getCowsList();
                    List<BuffaloesListRequestFields> buffaloesList = req.getDairyRequestFields().getBuffaloesList();
                    ArrayList<JSONObject> revenueofCows = new ArrayList<>();
                    ArrayList<JSONObject> revenueOfBuffaloes = new ArrayList<>();
                    for (CowsListRequestFields cow : cowList) {
                        int dailyRevenuePerCow = 0;
                        int monthlyRevenuePerCow = 0;
                        breedOfCow = cow.getBreedOfCows();
                        noOfCows = cow.getNoOfCows();
						logger.debug("breedOfCow : {}", breedOfCow);
						logger.debug("noOfCows : {}", noOfCows);
                        Integer cowYield = Integer.parseInt(lovContent.getJSONObject("Cow Yield").optString(breedOfCow, "0").trim());
						logger.debug("cowYield : {}", cowYield);
                        Integer cowCost = Integer.parseInt(locObj.getJSONObject("Cows").getString("Cost").trim());
						logger.debug("cowCost : {}", cowCost);
                        dailyRevenuePerCow = cowMilkRate * cowYield;
                        monthlyRevenuePerCow = (dailyRevenuePerCow - cowCost) * 30;
                        JSONObject revenue = new JSONObject();
                        revenue.put("breedOfCow", breedOfCow);
                        revenue.put("noOfCows", noOfCows);
                        revenue.put("dailyRevenuePerCow", dailyRevenuePerCow * noOfCows);
                        revenue.put("monthlyRevenuePerCow", monthlyRevenuePerCow * noOfCows);
                        monthlyDairyBusinessNetIncome += (monthlyRevenuePerCow * noOfCows);
                        revenueofCows.add(revenue);
                    }
                    for (BuffaloesListRequestFields buffalo : buffaloesList) {
                        int dailyRevenuePerBuffalo = 0;
                        int monthlyRevenuePerBuffalo = 0;
                        breedOfBuffaloes = buffalo.getBreedOfBuffaloes();
                        noOfBuffaloes = buffalo.getNoOfBuffaloes();
                        Integer buffaloYield = Integer.parseInt(lovContent.getJSONObject("Buffalo Yield").getString(breedOfBuffaloes).trim());
                        Integer buffaloCost = Integer.parseInt(locObj.getJSONObject("Buffaloes").getString("Cost").trim());
                        dailyRevenuePerBuffalo = buffaloMilkRate * buffaloYield;
                        monthlyRevenuePerBuffalo = (dailyRevenuePerBuffalo - buffaloCost) * 30;
                        JSONObject revenue = new JSONObject();
                        revenue.put("breedOfBuffalo", breedOfBuffaloes);
                        revenue.put("noOfBuffaloes", noOfBuffaloes);
                        revenue.put("dailyRevenuePerBuffalo", dailyRevenuePerBuffalo * noOfBuffaloes);
                        revenue.put("monthlyRevenuePerBuffalo", monthlyRevenuePerBuffalo * noOfBuffaloes);
                        monthlyDairyBusinessNetIncome += (monthlyRevenuePerBuffalo * noOfBuffaloes);
                        revenueOfBuffaloes.add(revenue);
                    }
                    double actualDairyIncome = monthlyDairyBusinessNetIncome * (1 - nonMilkingPeriod);
                    int actualMonthlyDairyBusinessNetIncome = (int) Math.round(actualDairyIncome);
                    logger.debug("monthlyDairyBusinessNetIncome : {}", monthlyDairyBusinessNetIncome);
                    logger.debug("actualMonthlyDairyBusinessNetIncome : {}", actualMonthlyDairyBusinessNetIncome);
                    resp.put("revenueOfCows", revenueofCows);
                    resp.put("revenueOfBuffaloes", revenueOfBuffaloes);
                    resp.put("netBusinessIncome", actualMonthlyDairyBusinessNetIncome);
                    break;
			case "TAILORING":
				String tailoringBusinessLocation = req.getTailoringRequestFields().getBusinessLocation().trim();
				locObj = lovContent.getJSONObject("Business_Location").getJSONObject(tailoringBusinessLocation);

				// JSONObject lovBusinessLocation = lov.getJSONObject("Business_Location");
				// String businessLocation =
				// req.getTailoringRequestFields().getBusinessLocation()String("businessLocation");
				String businessPremise = req.getTailoringRequestFields().getBusinessPremise();
				int numOfPowerTailoringMachine = req.getTailoringRequestFields().getNumOfPowerTailoringMachine();
				int numOfManualTailoringMachine = req.getTailoringRequestFields().getNumOfManualTailoringMachine();
				String isApplicantATailor = req.getTailoringRequestFields().getIsApplicantATailor();
				String isCoApplicantPartOfTailorBusiness = req.getTailoringRequestFields()
						.getIsCoApplicantPartOfTailorBusiness();
				int numOfSalariedTailors = req.getTailoringRequestFields().getNumOfSalariedTailors();
				String workType = req.getTailoringRequestFields().getWorkType();
				String stitchingFor = req.getTailoringRequestFields().getStitchingFor();

				int noOfTailorsToBeConsidered = 0;
				int powerMachinesToBeConsidered = 0;
				int manualMachinesToBeConsidered = 0;
				int powerMachineRevenuePerDay = 0;
				int manualMachineRevenuePerDay = 0;
				int revenuePerDay = 0;
				int revenuePerMonth = 0;
				Double totalMonthlyRevenue = 0.0;
				Double tailoringNetBusinessIncome = 0.0;
				int residualIncomeFactor = 0;

				noOfTailorsToBeConsidered = (isApplicantATailor.equalsIgnoreCase(Constants.YES) ? 1 : 0)
						+ (isCoApplicantPartOfTailorBusiness.equalsIgnoreCase(Constants.YES) ? 1 : 0)
						+ numOfSalariedTailors;
				powerMachinesToBeConsidered = numOfPowerTailoringMachine >= noOfTailorsToBeConsidered
						? noOfTailorsToBeConsidered
						: numOfPowerTailoringMachine;
				manualMachinesToBeConsidered = (noOfTailorsToBeConsidered < (numOfPowerTailoringMachine
						+ numOfManualTailoringMachine) ? noOfTailorsToBeConsidered
								: (numOfPowerTailoringMachine + numOfManualTailoringMachine))
						- powerMachinesToBeConsidered;
				powerMachineRevenuePerDay = powerMachinesToBeConsidered
						* locObj
								.getJSONObject("Premise Category Machinetype mapper")
								.getJSONObject(businessPremise).getJSONObject("Power")
								.getInt(stitchingFor)
						* lovContent.getJSONObject("Type").getInt(workType);
				manualMachineRevenuePerDay = manualMachinesToBeConsidered
						* locObj
								.getJSONObject("Premise Category Machinetype mapper")
								.getJSONObject(businessPremise).getJSONObject("Manual")
								.getInt(stitchingFor)
						* lovContent.getJSONObject("Type").getInt(workType);

				residualIncomeFactor = 100
						+ lovContent.getJSONObject("Premium Factor").getInt(businessPremise)
						+ lovContent.getJSONObject("Premise Factor").getInt(businessPremise)
						+ ((lovContent.getJSONObject("Additional Factor").getInt(
								"No of Machines/Labours") == (noOfTailorsToBeConsidered < (numOfPowerTailoringMachine
										+ numOfManualTailoringMachine) ? noOfTailorsToBeConsidered
												: (numOfPowerTailoringMachine + numOfManualTailoringMachine)))
														? lovContent.getJSONObject("Additional Factor")
																.getInt("Factor")
														: 0);

				revenuePerDay = powerMachineRevenuePerDay + manualMachineRevenuePerDay;
				revenuePerMonth = revenuePerDay
						* ((businessPremise.equalsIgnoreCase("House")) ? lovContent.getJSONObject("Month Days").getInt("House")
								: lovContent.getJSONObject("Month Days").getInt("Shop"));
				totalMonthlyRevenue = Double.parseDouble("" + (revenuePerMonth * residualIncomeFactor)) / 100;

				tailoringNetBusinessIncome = totalMonthlyRevenue
						- Double.parseDouble("" + (businessPremise.equalsIgnoreCase("Shop")
								? (locObj
										.getInt("Fixed Cost per month")
										+ ((totalMonthlyRevenue
												* locObj
														.getInt("Variable Cost per month")
												/ 100))
										+ (numOfSalariedTailors
												* locObj
														.getInt("Shop Salary per month")))
								: (/*locObj
										.getInt("Fixed Cost per month")
										+*/ (totalMonthlyRevenue
												* (Double.parseDouble(""+locObj
														.getInt("Variable Cost per month")) / 100))
										+ (numOfSalariedTailors
												* locObj
														.getInt("House Salary per month")))));
				
				
				logger.debug("test1 : {}", totalMonthlyRevenue);
				logger.debug("test2 : {}", Double.parseDouble(""+locObj
								.getInt("Variable Cost per month"))) ;
				logger.debug("test3 : {}", Double.parseDouble(""+(locObj
						.getInt("Variable Cost per month")/100))) ;
				logger.debug("test4 : {}", (totalMonthlyRevenue
						* (Double.parseDouble(""+locObj
								.getInt("Variable Cost per month")) / 100)));
				
				
				logger.debug("test5 : {}", (numOfSalariedTailors
						* locObj
						.getInt("House Salary per month")));
				resp.put("noOfTailorsToBeConsidered", noOfTailorsToBeConsidered);
				resp.put("powerMachinesToBeConsidered", powerMachinesToBeConsidered);
				resp.put("manualMachinesToBeConsidered", manualMachinesToBeConsidered);
				resp.put("powerMachineRevenuePerDay", powerMachineRevenuePerDay);
				resp.put("manualMachineRevenuePerDay", manualMachineRevenuePerDay);
				
				
				resp.put("revenuePerDay", Math.round(revenuePerDay));
				resp.put("revenuePerMonth", Math.round(revenuePerMonth));
				resp.put("totalMonthlyIncome", Math.round(totalMonthlyRevenue));
				resp.put("netBusinessIncome", Math.round(tailoringNetBusinessIncome));
				

				break;
			case "KIRANA":
				String kiranaBusinessLocation = req.getKiranaRequestFields().getBusinessLocation().trim();
				locObj = lovContent.getJSONObject("Business_Location").getJSONObject(kiranaBusinessLocation);
				String marketClassification = req.getKiranaRequestFields().getMarketClassification().trim();
				Double areaOfTheShop = Double
						.parseDouble((req.getKiranaRequestFields().getAreaOfTheShop().split("\\|")[0]).trim());
				logger.debug("areaOfTheShop : " + areaOfTheShop);
				String metricOfTheShop = (req.getKiranaRequestFields().getAreaOfTheShop().split("\\|")[1]).trim();
				logger.debug("metricOfTheShop : " + metricOfTheShop);
				Double areaOfTheGodown = Double
						.parseDouble((req.getKiranaRequestFields().getAreaOfTheGodown().split("\\|")[0]).trim());
				logger.debug("areaOfTheGodown : " + areaOfTheGodown);
				String metricOfTheGodown = (req.getKiranaRequestFields().getAreaOfTheGodown().split("\\|")[1]).trim();
				logger.debug("metricOfTheGodown : " + metricOfTheGodown);
				String occupancyLevelOfShopAndGodown = req.getKiranaRequestFields().getOccupancyLevelOfShopAndGodown()
						.trim();
				Double monthlyNetBusinessIncome = Double
						.parseDouble(req.getKiranaRequestFields().getMonthlyNetBusinessIncome().toString().trim());
				Double areaOfTheShopInSqft = areaOfTheShop
						* Double.parseDouble(lovContent.getJSONObject("Conversion Metric").getString(metricOfTheShop));
				Double areaOfTheGodownInSqft = areaOfTheGodown * Double
						.parseDouble(lovContent.getJSONObject("Conversion Metric").getString(metricOfTheGodown));
				Double totalArea = areaOfTheShopInSqft + areaOfTheGodownInSqft;
				Double inventoryArea = totalArea * Double.parseDouble(lovContent.getJSONObject("Occupancy")
						.getJSONObject(occupancyLevelOfShopAndGodown).getString("Inventory Area Multiplier"));
				Double inventoryValue = inventoryArea * Double.parseDouble(locObj.get("Per sqft value").toString());
				Double monthlySales = ((inventoryValue * Double.parseDouble(lovContent.getJSONObject("Occupancy")
						.getJSONObject(occupancyLevelOfShopAndGodown).getString("Sales to Inventory Ratio")))
						+ Double.parseDouble(locObj.get("Fixed Overhead Sales").toString()))
						* Double.parseDouble(
								lovContent.getJSONObject("Market Classification").getString(marketClassification));
				Double calculatedNetIncome = monthlySales
						* (Double.parseDouble(lovContent.getString("Profit Margin")) / 100);
				Double actualIncome = monthlyNetBusinessIncome;
				Double finalNetIncome = Math.min(calculatedNetIncome, actualIncome);
				resp.put("areaOfTheShopInSqft",  areaOfTheShopInSqft);
				resp.put("areaOfTheGodownInSqft",  areaOfTheGodownInSqft);
				resp.put("totalArea",  Math.round(totalArea));
				resp.put("inventoryArea",  Math.round(inventoryArea));
				resp.put("inventoryValue",  Math.round(inventoryValue));

				resp.put("monthlySales",  Math.round(monthlySales));
				resp.put("calculatedNetIncome",  Math.round(calculatedNetIncome));
				resp.put("actualIncome",  Math.round(actualIncome));
				resp.put("finalNetIncome",  Math.round(finalNetIncome));
				break;
			case "WAGE":
				BranchWageDetails branchWageDetail = branchWageDetailsRepo
						.findByBranchId(req.getWageRequestFields().getBranchId());
				int workingDays = req.getWageRequestFields().getWorkingDays();
				int dailyWage;
				if ("male".equalsIgnoreCase(req.getWageRequestFields().getGender().trim().toLowerCase())) {
					dailyWage = Integer.parseInt(branchWageDetail.getMaleWage());
				} else {
					dailyWage = Integer.parseInt(branchWageDetail.getFemaleWage());
				}
				resp.put("wageEarnedPerDay", dailyWage);
				resp.put("totalWagePerMonth", dailyWage * workingDays);
				break;
			case "RENTALINCOME":
				String state = req.getRentalIncomeRequestFields().getState().trim().toUpperCase();
				String buildingType = req.getRentalIncomeRequestFields().getTypeOfBuilding();
				Double rentalIncome = Double.parseDouble(""+req.getRentalIncomeRequestFields().getRentalIncome());
				Double minRent = (rentalIncome < Double.parseDouble(lovContent.getJSONObject(state).get(buildingType).toString())) ? rentalIncome
						: lovContent.getJSONObject(state).getInt(buildingType);
				logger.debug("minRent : " + minRent);
				Double consideredMonthlyIncome = minRent * (Double.parseDouble(lovContent.get("consideredLimit").toString()) / 100);
				logger.debug("consideredMonthlyIncome : " + consideredMonthlyIncome);
				resp.put("rentalIncome",  Math.round(minRent));
				resp.put("consideredMonthlyIncome",  Math.round(consideredMonthlyIncome));
				break;
			case "AGRICULTURE":
				String agricultureState = req.getAgricultureRequestFields().getState().trim().toUpperCase();
				String cropType = req.getAgricultureRequestFields().getCropType().trim();
				String crop = req.getAgricultureRequestFields().getCrop().trim();
				Double maxProductionPerAcre = Double
						.parseDouble(req.getAgricultureRequestFields().getMaxProductionPerAcre().trim());
				logger.debug("maxProductionPerAcre : " + maxProductionPerAcre);
				Double totalAcres = Double.parseDouble(req.getAgricultureRequestFields().getTotalAcres().trim());
				logger.debug("totalAcres : " + totalAcres);
				logger.debug("req : " + req.toString());
				logger.debug("lovContent : " + lovContent.toString());
				logger.debug("Rate : " + Double.parseDouble(lovContent.getJSONObject(cropType).get("Rate").toString()));
				logger.debug("Margin : " + (Double.parseDouble(lovContent.getJSONObject(cropType).get("Margin").toString())));
				Double totalAgriIncome = totalAcres * maxProductionPerAcre
						* Double.parseDouble(lovContent.getJSONObject(cropType).get("Rate").toString())
						* (Double.parseDouble(lovContent.getJSONObject(cropType).get("Margin").toString()) / 100);
				logger.debug("totalAgriIncome : " + totalAgriIncome);
				Double consideredAgriIncome = (totalAgriIncome / 12) * (Double.parseDouble(lovContent.get("consideredLimit").toString()) / 100);
				logger.debug("consideredAgriIncome : " + consideredAgriIncome);
				resp.put("totalAgriIncome",  Math.round(totalAgriIncome));
				resp.put("consideredAgriIncome",  Math.round(consideredAgriIncome));
				break;
			}
		} catch (Exception e) {
			
			logger.error("Exception : ", e);
			
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj("");
			incomeAssessmentCheckResponse.setResponseBody(responseBody);
			return incomeAssessmentCheckResponse;
		}
		Gson gson = new Gson();
		String response = gson.toJson(resp);
		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
		responseBody.setResponseObj(response);
		incomeAssessmentCheckResponse.setResponseBody(responseBody);
		return incomeAssessmentCheckResponse;
	}

	private Response incomeAssessmentCheckFallback(IncomeCalculatorRequest incomeCalculatorRequest, Exception e) {
		logger.error("incomeAssessmentCheckFallback error : ", e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
}
