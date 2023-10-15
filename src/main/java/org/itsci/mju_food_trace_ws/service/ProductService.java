package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itsci.mju_food_trace_ws.model.Product;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface ProductService {

    List<Product> getAllProducts ();
    List<Product> getProductsByUsername (String username);
    Product getProductById (String productId);
    Product addProduct (Map<String, String> map) throws JsonProcessingException, NoSuchAlgorithmException;
    Product updateProduct (Product product);
    Map<String, String> getProductExistingByManufacturerUsername (String username);
    void deleteProduct (String productId);
    String getNewPdCurrBlockHash (String productId) throws JsonProcessingException, NoSuchAlgorithmException;

}
