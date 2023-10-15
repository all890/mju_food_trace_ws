package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface RawMaterialShippingService {

    RawMaterialShipping addRawMaterialShipping(Map<String, String> map) throws ParseException, NoSuchAlgorithmException, JsonProcessingException;
    List<RawMaterialShipping> getListAllSentAgriByUsername(String username);
    List<RawMaterialShipping> getListAllSentAgriByFarmerUsername(String username);
    RawMaterialShipping getRawMaterialShippingById(String rawMatShpId);
    Map<String, String> getRmsExistingByManufacturerUsername (String username);
    Map<String, Double> getRemainNetQtyOfRmsByManufacturerUsername (String username);
    double getRemainNetQtyFromManufacturingByManufacturingId (String manufacturingId);
    boolean isRmsAndPlantingChainValid(String rawMatShpId) throws JsonProcessingException, NoSuchAlgorithmException;
    String testGetHash (String rawMatShpId) throws NoSuchAlgorithmException, JsonProcessingException;
    String getNewRmsCurrBlockHash (String rawMatShpId) throws JsonProcessingException, NoSuchAlgorithmException;
}
