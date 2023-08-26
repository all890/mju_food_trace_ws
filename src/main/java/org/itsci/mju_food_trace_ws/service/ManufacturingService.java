package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ManufacturingService {

    List<Manufacturing> getAllManufacturing ();
    List<Manufacturing> getManufacturingByProductId (String productId);
    Manufacturing getManufacturingById (String manufacturingId);
    Manufacturing addManufacturing (Map<String, String> map)throws ParseException;
    Manufacturing updateManufacturing (Manufacturing manufacturing);
    void deleteManufacturing (String manufacturingId);
    Manufacturing recordManufacturing (String manufacturingId) throws JsonProcessingException, NoSuchAlgorithmException;
    List<Manufacturing> getListAllManufacturingByUsername(String username);


}
