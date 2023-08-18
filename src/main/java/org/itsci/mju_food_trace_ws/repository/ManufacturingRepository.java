package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManufacturingRepository extends JpaRepository<Manufacturing, String> {

    List<Manufacturing> getManufacturingsByProduct_ProductId (String productId);

}
