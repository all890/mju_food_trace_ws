package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlantingRepository extends JpaRepository<Planting, String> {

    List<Planting> getPlantingsByFarmerCertificate_Farmer_User_Username (String farmerId);
    boolean existsByPlantingImg (String plantingImg);

    @Query(value = "SELECT p.plantingId FROM plantings p ORDER BY p.plantingId DESC LIMIT 1", nativeQuery = true)
    String getMaxPlantingId();
}
