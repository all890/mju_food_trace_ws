package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {

    List<Product> getAllProducts ();
    List<Product> getProductsByUsername (String username);
    Product getProductById (String productId);
    Product addProduct (Map<String, String> map);
    Product updateProduct (Product product);
    void deleteProduct (String productId);

}
