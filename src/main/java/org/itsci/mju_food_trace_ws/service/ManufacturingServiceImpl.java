package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.model.Product;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.repository.ManufacturingRepository;
import org.itsci.mju_food_trace_ws.repository.ProductRepository;
import org.itsci.mju_food_trace_ws.repository.RawMaterialShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ManufacturingServiceImpl implements ManufacturingService {

    @Autowired
    private ManufacturingRepository manufacturingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RawMaterialShippingRepository rawMaterialShippingRepository;
    @Override
    public List<Manufacturing> getAllManufacturing() {
        return manufacturingRepository.findAll();
    }

    @Override
    public List<Manufacturing> getManufacturingByProductId(String productId) {
        return manufacturingRepository.getManufacturingsByProduct_ProductId(productId);
    }

    @Override
    public Manufacturing getManufacturingById(String manufacturingId) {
        return manufacturingRepository.getReferenceById(manufacturingId);
    }

    @Override
    public Manufacturing addManufacturing(Map<String, String> map) throws ParseException {
        Manufacturing manufacturing = null;

        String maxManufacturingId = manufacturingRepository.getMaxManufacturingId();
        long maxManufacturingLong = 0;

        if (maxManufacturingId != null) {
            maxManufacturingLong = Long.parseLong(maxManufacturingId.substring(2));
        }
        //Planting session
        String manufacturingId = generateManufacturingId(maxManufacturingLong + 1);
        String manufactureDatestr = map.get("manufactureDate");
        String expireDatestr = map.get("expireDate");

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date manufactureDate = format.parse(manufactureDatestr);
        Date expireDate = format.parse(expireDatestr);

        int productQty = Integer.parseInt(map.get("productQty"));
        String productUnit = map.get("productUnit");
        double usedRawMatQty = Double.parseDouble(map.get("usedRawMatQty"));
        String usedRawMatQtyUnit = map.get("usedRawMatQtyUnit");
        String rawMaterialShippingId = map.get("rawMaterialShippingId");
        RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(rawMaterialShippingId);

        String manuftPrevBlockHash = rawMaterialShipping.getRmsCurrBlockHash();
        String manuftCurrBlockHash = null;



        String productId = map.get("productId");
        System.out.println("productid is :"+productId);
        Product product = productRepository.getReferenceById(productId);

        manufacturing = new Manufacturing(manufacturingId,manufactureDate,expireDate,productQty,productUnit,usedRawMatQty,usedRawMatQtyUnit,
                manuftPrevBlockHash,manuftCurrBlockHash,rawMaterialShipping,product);



        return manufacturingRepository.save(manufacturing);
    }

    @Override
    public Manufacturing updateManufacturing(Manufacturing manufacturing) {
        return manufacturingRepository.save(manufacturing);
    }

    @Override
    public void deleteManufacturing(String manufacturingId) {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);
        manufacturing.setRawMaterialShipping(null);
        manufacturing.setProduct(null);
        manufacturingRepository.delete(manufacturing);
    }

    @Override
    public Manufacturing recordManufacturing(String manufacturingId) throws JsonProcessingException, NoSuchAlgorithmException {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);

        String jsonStr = new ObjectMapper().writeValueAsString(manufacturing);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedManuftCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        manufacturing.setManuftCurrBlockHash(encodedManuftCurrBlockHash);

        return manufacturingRepository.save(manufacturing);
    }


    @Override
    public List<Manufacturing> getListAllManufacturingByUsername(String username) {
        return manufacturingRepository.getManufacturingsByProduct_Manufacturer_User_Username(username);
    }

    public String generateManufacturingId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "MF" + result;
        return result;
    }
}
