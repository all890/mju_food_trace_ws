package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;

import java.util.List;

public interface RawMaterialShippingService {


    List<RawMaterialShipping> getListAllSentAgriByUsername(String username);
}
