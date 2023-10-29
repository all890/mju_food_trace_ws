package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ManufacturingRepository extends JpaRepository<Manufacturing, String> {

    List<Manufacturing> getManufacturingsByProduct_ProductId (String productId);
    List<Manufacturing> getManufacturingsByRawMaterialShipping_RawMatShpId (String rawMatShpId);

    @Query(value = "SELECT m.manufacturingId FROM manufacturings m ORDER BY m.manufacturingId DESC LIMIT 1", nativeQuery = true)
    String getMaxManufacturingId();
    List<Manufacturing> getManufacturingsByProduct_Manufacturer_User_Username (String username);
    boolean existsManufacturingByRawMaterialShipping_RawMatShpId (String rawMatShpId);
}
