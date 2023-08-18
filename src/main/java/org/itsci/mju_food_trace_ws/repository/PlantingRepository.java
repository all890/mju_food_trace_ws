package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlantingRepository extends JpaRepository<Planting, String> {

    List<Planting> getPlantingsByFarmer_User_Username (String farmerId);
    boolean existsByPlantingImg (String plantingImg);

    @Query(value = "SELECT p.planting_id FROM plantings p ORDER BY p.planting_id DESC LIMIT 1", nativeQuery = true)
    String getMaxPlantingId();
}
