package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {

    List<Manufacturer> getManufacturersByManuftRegStatus (String farmerRegStatus);

    Manufacturer getManufacturerByUser_Username (String username);

    Manufacturer getManufacturerByManuftNameEquals (String manuftName);
}
