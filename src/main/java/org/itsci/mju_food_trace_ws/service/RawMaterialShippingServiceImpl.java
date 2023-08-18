package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.repository.RawMaterialShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RawMaterialShippingServiceImpl implements RawMaterialShippingService{
  @Autowired
  private RawMaterialShippingRepository rawMaterialShippingRepository;
    @Override
    public List<RawMaterialShipping> getListAllSentAgriByUsername(String username) {
        return rawMaterialShippingRepository.getRawMaterialShippingsByManufacturer_User_Username(username);
    }
}
