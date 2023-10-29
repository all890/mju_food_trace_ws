package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> getProductsByManufacturer_User_Username (String username);

    @Query(value = "SELECT p.productId FROM products p ORDER BY p.productId DESC LIMIT 1", nativeQuery = true)
    String getMaxProductId ();

}
