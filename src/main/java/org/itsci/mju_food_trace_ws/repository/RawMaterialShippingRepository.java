package org.itsci.mju_food_trace_ws.repository;


import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawMaterialShippingRepository extends JpaRepository<RawMaterialShipping, String> {

    List<RawMaterialShipping> getRawMaterialShippingsByManufacturer_User_Username (String username);
}
