package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface FarmerService {

    List<Farmer> getAllFarmers();
    List<Farmer> getFarmersByRegistStat();
    Farmer getFarmerById(String farmerId);
    Farmer saveFarmer(Map<String, String> map) throws ParseException, NoSuchAlgorithmException, JsonProcessingException;
    Farmer updateFarmer(Farmer farmer);
    Farmer updateFmRegistStatus(String farmerId) throws JsonProcessingException, NoSuchAlgorithmException;
    Farmer declineFmRegistStatus(String farmerId);
    FarmerCertificate getFarmerDetails(String farmerId);
    void deleteFarmer(String farmerId);

    Farmer getFarmerByUsername(String username);

    Farmer getFarmerByFarmerMobileNo (String farmerMobileNo);

    String getNewFmCurrBlockHash (String farmerId) throws JsonProcessingException, NoSuchAlgorithmException;

}
