package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
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

        //Planting planting = manufacturing.getRawMaterialShipping().getPlanting();
        //RawMaterialShipping rawMaterialShipping = manufacturing.getRawMaterialShipping();

        if (manufacturing.getProduct().getPdCurrBlockHash() == null) {
            User tempUser = manufacturing.getProduct().getManufacturer().getUser();
            manufacturing.getProduct().getManufacturer().setUser(null);

            String jsonStr4 = new ObjectMapper().writeValueAsString(manufacturing.getProduct());
            MessageDigest digest4 = MessageDigest.getInstance("SHA-256");
            byte[] hash4 = digest4.digest(jsonStr4.getBytes(StandardCharsets.UTF_8));
            String encodedPdCurrBlockHash = Base64.getEncoder().encodeToString(hash4);

            manufacturing.getProduct().setPdCurrBlockHash(encodedPdCurrBlockHash);
            manufacturing.getProduct().getManufacturer().setUser(tempUser);
        }

        User tempUser2 = manufacturing.getProduct().getManufacturer().getUser();
        manufacturing.getProduct().getManufacturer().setUser(null);

        User tempUser = manufacturing.getRawMaterialShipping().getPlanting().getFarmer().getUser();
        manufacturing.getRawMaterialShipping().getPlanting().getFarmer().setUser(null);

        String jsonStr3 = new ObjectMapper().writeValueAsString(manufacturing);
        MessageDigest digest3 = MessageDigest.getInstance("SHA-256");
        byte[] hash3 = digest3.digest(jsonStr3.getBytes(StandardCharsets.UTF_8));
        String encodedManuftCurrBlockHash = Base64.getEncoder().encodeToString(hash3);

        manufacturing.setManuftCurrBlockHash(encodedManuftCurrBlockHash);
        manufacturing.getProduct().getManufacturer().setUser(tempUser2);
        manufacturing.getRawMaterialShipping().getPlanting().getFarmer().setUser(tempUser);

        return manufacturingRepository.save(manufacturing);

        /*
        //First step : checking the current hash of planting
        String tempPtCurrHash = planting.getPtCurrBlockHash();
        planting.setPtCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        System.out.println("1 OLD HASH : " + tempPtCurrHash);
        System.out.println("1 NEW HASH : " + encodedPtCurrBlockHash);

        if (tempPtCurrHash.equals(encodedPtCurrBlockHash)) {
            if (rawMaterialShipping.getRmsPrevBlockHash().equals(tempPtCurrHash)) {
                planting.setPtCurrBlockHash(tempPtCurrHash);

                String tempRmsCurrHash = rawMaterialShipping.getRmsCurrBlockHash();
                rawMaterialShipping.setRmsCurrBlockHash(null);

                String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
                MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
                byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
                String encodedPtCurrBlockHash2 = Base64.getEncoder().encodeToString(hash2);

                System.out.println("3 OLD HASH : " + tempRmsCurrHash);
                System.out.println("3 NEW HASH : " + encodedPtCurrBlockHash2);

                if (tempRmsCurrHash.equals(encodedPtCurrBlockHash2)) {
                    if (manufacturing.getManuftPrevBlockHash().equals(tempRmsCurrHash)) {

                        rawMaterialShipping.setRmsCurrBlockHash(tempRmsCurrHash);


                    } else {
                        System.out.println("ERROR FOURTH FLOOR!");
                        return null;
                    }
                } else {
                    System.out.println("ERROR THIRD FLOOR!");
                    return null;
                }
            } else {
                System.out.println("ERROR SECOND FLOOR!");
                return null;
            }
        } else {
            System.out.println("ERROR FIRST FLOOR!");
            return null;
        }
        */
    }


    @Override
    public List<Manufacturing> getListAllManufacturingByUsername(String username) {
        return manufacturingRepository.getManufacturingsByProduct_Manufacturer_User_Username(username);
    }

    @Override
    public String getNewManuftCurrBlockHash(String manufacturingId) throws JsonProcessingException, NoSuchAlgorithmException {
        Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);
        manufacturing.getProduct().getManufacturer().setUser(null);
        manufacturing.getRawMaterialShipping().getPlanting().getFarmer().setUser(null);
        manufacturing.setManuftCurrBlockHash(null);

        String jsonStr = new ObjectMapper().writeValueAsString(manufacturing);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
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
