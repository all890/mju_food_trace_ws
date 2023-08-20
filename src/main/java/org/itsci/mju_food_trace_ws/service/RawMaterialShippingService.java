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
    RawMaterialShipping getRawMaterialShippingById(String rawMatShpId);
    String testGetHash (String rawMatShpId) throws NoSuchAlgorithmException, JsonProcessingException;
}
