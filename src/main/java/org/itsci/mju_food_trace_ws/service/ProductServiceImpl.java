package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturingRepository;
import org.itsci.mju_food_trace_ws.repository.ProductRepository;
import org.itsci.mju_food_trace_ws.utils.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ManufacturingRepository manufacturingRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByUsername(String username) {
        return productRepository.getProductsByManufacturer_User_Username(username);
    }

    @Override
    public Product getProductById(String productId) {
        return productRepository.getReferenceById(productId);
    }

    @Override
    public Product addProduct(Map<String, String> map) throws JsonProcessingException, NoSuchAlgorithmException {

        Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(map.get("username"));

        String maxProdId = productRepository.getMaxProductId();
        long maxProdLong = 0;

        if (maxProdId != null) {
            maxProdLong = Long.parseLong(maxProdId.substring(2));
        }

        String productId = generateProductId(maxProdLong + 1);
        String productName = map.get("productName");
        int netVolume = Integer.parseInt(map.get("netVolume"));
        int netEnergy = Integer.parseInt(map.get("netEnergy"));
        int saturatedFat = Integer.parseInt(map.get("saturatedFat"));
        int cholesterol = Integer.parseInt(map.get("cholesterol"));
        int protein = Integer.parseInt(map.get("protein"));
        int sodium = Integer.parseInt(map.get("sodium"));
        int fiber = Integer.parseInt(map.get("fiber"));
        int sugar = Integer.parseInt(map.get("sugar"));
        int vitA = Integer.parseInt(map.get("vitA"));
        int vitB1 = Integer.parseInt(map.get("vitB1"));
        int vitB2 = Integer.parseInt(map.get("vitB2"));
        int iron = Integer.parseInt(map.get("iron"));
        int calcium = Integer.parseInt(map.get("calcium"));

        Product product = new Product(productId, productName, netVolume, netEnergy, saturatedFat, cholesterol,
                protein, sodium, fiber, sugar, vitA, vitB1, vitB2, iron, calcium, manufacturer.getMnCurrBlockHash(), null, manufacturer);

        User tempMnUser = product.getManufacturer().getUser();
        product.getManufacturer().setUser(null);
        String pdCurrBlockHash = HashUtil.hashSHA256(product);
        product.getManufacturer().setUser(tempMnUser);

        product.setPdCurrBlockHash(pdCurrBlockHash);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Map<String, String> getProductExistingByManufacturerUsername(String username) {
        Map<String, String> prodExisting = new HashMap<>();
        List<Product> products = productRepository.getProductsByManufacturer_User_Username(username);

        for (Product product : products) {
            List<Manufacturing> manufacturings = manufacturingRepository.getManufacturingsByProduct_Manufacturer_User_Username(username);
            for (Manufacturing manufacturing : manufacturings) {
                if (product.getProductId().equals(manufacturing.getProduct().getProductId())) {
                    prodExisting.put(product.getProductId(), "was manufactured");
                    break;
                }
            }
        }

        return prodExisting;
    }

    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.getReferenceById(productId);
        product.setManufacturer(null);
        productRepository.delete(product);
    }

    @Override
    public boolean isChainBeforeProductValid(String username) throws NoSuchAlgorithmException, JsonProcessingException {
        Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(username);

        User tempMnUser = manufacturer.getUser();
        String oldMnCurrBlockHash = manufacturer.getMnCurrBlockHash();
        manufacturer.setMnCurrBlockHash(null);
        manufacturer.setUser(null);

        String newMnCurrBlockHash = HashUtil.hashSHA256(manufacturer);

        manufacturer.setMnCurrBlockHash(oldMnCurrBlockHash);
        manufacturer.setUser(tempMnUser);

        return newMnCurrBlockHash.equals(oldMnCurrBlockHash);
    }

    public String generateProductId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "PD" + result;
        return result;
    }
}
