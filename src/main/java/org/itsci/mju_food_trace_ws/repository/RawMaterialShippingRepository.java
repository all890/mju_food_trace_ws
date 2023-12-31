package org.itsci.mju_food_trace_ws.repository;


import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RawMaterialShippingRepository extends JpaRepository<RawMaterialShipping, String> {

    List<RawMaterialShipping> getRawMaterialShippingsByManufacturer_User_Username (String username);
    List<RawMaterialShipping> getRawMaterialShippingsByPlanting_FarmerCertificate_Farmer_User_Username (String username);

    @Query(value = "SELECT rms.rawMatShpId FROM raw_material_shippings rms ORDER BY rms.rawMatShpId DESC LIMIT 1", nativeQuery = true)
    String getMaxRawMaterialShippingId();

    List<RawMaterialShipping> getRawMaterialShippingsByPlanting_PlantingId (String plantingId);
}
