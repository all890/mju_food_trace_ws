package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmerRepository extends JpaRepository<Farmer, String> {

    List<Farmer> getFarmersByFarmerRegStatus (String farmerRegStatus);

    Farmer getFarmerByUser_Username (String username);

}
