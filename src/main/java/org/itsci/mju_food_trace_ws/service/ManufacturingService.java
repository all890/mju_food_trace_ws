package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Manufacturing;

import java.util.List;
import java.util.Map;

public interface ManufacturingService {

    List<Manufacturing> getAllManufacturing ();
    List<Manufacturing> getManufacturingByProductId (String productId);
    Manufacturing getManufacturingById (String manufacturingId);
    Manufacturing addManufacturing (Map<String, String> map);
    Manufacturing updateManufacturing (Manufacturing manufacturing);
    void deleteManufacturing (String manufacturingId);

}
