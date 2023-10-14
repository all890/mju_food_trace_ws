package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturingRepository;
import org.itsci.mju_food_trace_ws.repository.PlantingRepository;
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
public class RawMaterialShippingServiceImpl implements RawMaterialShippingService {

  @Autowired
  private RawMaterialShippingRepository rawMaterialShippingRepository;

  @Autowired
  private PlantingRepository plantingRepository;

  @Autowired
  private ManufacturerRepository manufacturerRepository;

  @Autowired
  private ManufacturingRepository manufacturingRepository;

  @Override
  public RawMaterialShipping addRawMaterialShipping(Map<String, String> map) throws ParseException, NoSuchAlgorithmException, JsonProcessingException {

    String manuftId = map.get("manuftId");
    String plantingId = map.get("plantingId");

    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    Date rawMatShpDate = format.parse(map.get("rawMatShpDate"));

    System.out.println(rawMatShpDate);

    double rawMatShpQty = Double.parseDouble(map.get("rawMatShpQty"));
    String rawMatShpQtyUnit = map.get("rawMatShpQtyUnit");

    Planting planting = plantingRepository.getReferenceById(plantingId);

    Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);

    User tempFmUser = planting.getFarmer().getUser();
    User tempMnUser = manufacturer.getUser();

    planting.getFarmer().setUser(null);
    manufacturer.setUser(null);

    String maxRawMatShpId = rawMaterialShippingRepository.getMaxRawMaterialShippingId();
    long maxRmsLong = 0;

    if (maxRawMatShpId != null) {
      maxRmsLong = Long.parseLong(maxRawMatShpId.substring(3));
    }
    String rawMatShpId = generateRawMaterialShippingId(maxRmsLong + 1);

    RawMaterialShipping rawMaterialShipping = new RawMaterialShipping(rawMatShpId, rawMatShpDate, rawMatShpQty, rawMatShpQtyUnit, planting.getPtCurrBlockHash(), null, planting, manufacturer);

    String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
    MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
    byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
    String encodedRmsCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

    rawMaterialShipping.setRmsCurrBlockHash(encodedRmsCurrBlockHash);
    rawMaterialShipping.getPlanting().getFarmer().setUser(tempFmUser);
    rawMaterialShipping.getManufacturer().setUser(tempMnUser);

    return rawMaterialShippingRepository.save(rawMaterialShipping);
    //First step: query all rms which have plantingId equals to determined plantingId
    //List<RawMaterialShipping> rmsContPtId = rawMaterialShippingRepository.getRawMaterialShippingsByPlanting_PlantingId(plantingId);

    //double sumOfRmsGrams = 0;

    /*
    for (RawMaterialShipping rms : rmsContPtId) {
      if (rms.getRawMatShpQtyUnit().equals("กิโลกรัม")) {
        sumOfRmsGrams += rms.getRawMatShpQty() * 1000;
      } else {
        sumOfRmsGrams += rms.getRawMatShpQty();
      }
    }
    */

    /*
    double ptNetQtyGrams = 0;

    if (planting.getNetQuantityUnit().equals("กิโลกรัม")) {
      ptNetQtyGrams = planting.getNetQuantity() * 1000;
    } else {
      ptNetQtyGrams = planting.getNetQuantity();
    }

    //If sum of rms grams isn't greater than pt net qty grams, then hash
    if (sumOfRmsGrams <= ptNetQtyGrams) {
      if (planting.getPtCurrBlockHash() == null) {
        String jsonStr = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
        String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

        planting.setPtCurrBlockHash(encodedPtCurrBlockHash);
        plantingRepository.save(planting);
      }

      Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);

      String maxRawMatShpId = rawMaterialShippingRepository.getMaxRawMaterialShippingId();
      long maxRmsLong = 0;

      if (maxRawMatShpId != null) {
        maxRmsLong = Long.parseLong(maxRawMatShpId.substring(3));
      }
      String rawMatShpId = generateRawMaterialShippingId(maxRmsLong + 1);

      RawMaterialShipping rawMaterialShipping = new RawMaterialShipping(rawMatShpId, rawMatShpDate, rawMatShpQty, rawMatShpQtyUnit, planting.getPtCurrBlockHash(), null, planting, manufacturer);

      String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
      MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
      byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
      String encodedRmsCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

      rawMaterialShipping.setRmsCurrBlockHash(encodedRmsCurrBlockHash);

      return rawMaterialShippingRepository.save(rawMaterialShipping);
    } else {
      return null;
    }
    */

  }

  @Override
  public List<RawMaterialShipping> getListAllSentAgriByUsername(String username) {
    return rawMaterialShippingRepository.getRawMaterialShippingsByManufacturer_User_Username(username);
  }

  @Override
  public RawMaterialShipping getRawMaterialShippingById(String rawMatShpId) {
    return rawMaterialShippingRepository.getReferenceById(rawMatShpId);
  }

  @Override
  public Map<String, String> getRmsExistingByManufacturerUsername(String username) {

    Map<String, String> rmsExists = new HashMap<>();

    List<RawMaterialShipping> rawMaterialShippings = rawMaterialShippingRepository.getRawMaterialShippingsByManufacturer_User_Username(username);

    for (RawMaterialShipping rms : rawMaterialShippings) {
      if (manufacturingRepository.existsManufacturingByRawMaterialShipping_RawMatShpId(rms.getRawMatShpId())) {
        rmsExists.put(rms.getRawMatShpId(), "exist");
      }
    }

    return rmsExists;
  }

  @Override
  public Map<String, Double> getRemainNetQtyOfRmsByManufacturerUsername(String username) {
    Map<String, Double> remNetQty = new HashMap<>();
    List<RawMaterialShipping> rawMaterialShippings = rawMaterialShippingRepository.getRawMaterialShippingsByManufacturer_User_Username(username);

    for (RawMaterialShipping rms : rawMaterialShippings) {
      double sumOfManuftQtyGrams = 0.0;
      List<Manufacturing> manufacturings = manufacturingRepository.getManufacturingsByRawMaterialShipping_RawMatShpId(rms.getRawMatShpId());
      for (Manufacturing manufacturing : manufacturings) {
        if (manufacturing.getUsedRawMatQtyUnit().equals("กิโลกรัม")) {
          sumOfManuftQtyGrams += manufacturing.getUsedRawMatQty() * 1000.0;
        } else {
          sumOfManuftQtyGrams += manufacturing.getUsedRawMatQty();
        }
      }

      if (rms.getRawMatShpQtyUnit().equals("กิโลกรัม")) {
        remNetQty.put(rms.getRawMatShpId(), (rms.getRawMatShpQty() * 1000.0) - sumOfManuftQtyGrams);
      } else {
        remNetQty.put(rms.getRawMatShpId(), rms.getRawMatShpQty() - sumOfManuftQtyGrams);
      }

    }

    return remNetQty;
  }

  @Override
  public double getRemainNetQtyFromManufacturingByManufacturingId(String manufacturingId) {

    double sumRmsQty = 0.0;
    double sumUsedRawMatQty = 0.0;

    double mainQty = 0.0;

    Manufacturing manufacturing = manufacturingRepository.getReferenceById(manufacturingId);

    mainQty = manufacturing.getUsedRawMatQtyUnit().equals("กิโลกรัม") ? manufacturing.getUsedRawMatQty() * 1000.0 : manufacturing.getUsedRawMatQty();

    RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(manufacturing.getRawMaterialShipping().getRawMatShpId());
    List<Manufacturing> manufacturings = manufacturingRepository.getManufacturingsByRawMaterialShipping_RawMatShpId(rawMaterialShipping.getRawMatShpId());

    sumRmsQty = rawMaterialShipping.getRawMatShpQtyUnit().equals("กิโลกรัม") ? rawMaterialShipping.getRawMatShpQty() * 1000.0 : rawMaterialShipping.getRawMatShpQty();
    for (Manufacturing m : manufacturings) {
      if (m.getUsedRawMatQtyUnit().equals("กิโลกรัม")) {
        sumUsedRawMatQty += m.getUsedRawMatQty() * 1000.0;
      } else {
        sumUsedRawMatQty += m.getUsedRawMatQty();
      }
    }

    return sumRmsQty - sumUsedRawMatQty + mainQty;
  }

  @Override
  public boolean isRmsAndPlantingChainValid(String rawMatShpId) throws JsonProcessingException, NoSuchAlgorithmException {
    RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(rawMatShpId);

    //TODO: check curr hash of planting
    Planting planting = rawMaterialShipping.getPlanting();
    String tempPtCurrHash = planting.getPtCurrBlockHash();
    planting.setPtCurrBlockHash(null);

    String jsonStr = new ObjectMapper().writeValueAsString(planting);
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
    String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

    System.out.println("1 OLD HASH : " + tempPtCurrHash);
    System.out.println("1 NEW HASH : " + encodedPtCurrBlockHash);

    if (tempPtCurrHash.equals(encodedPtCurrBlockHash)) {
      //TODO: check curr hash of planting and prev hash of rms
      if (rawMaterialShipping.getRmsPrevBlockHash().equals(tempPtCurrHash)) {
        //TODO: check curr hash of rms
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
          return true;
        } else {
          System.out.println("ERROR THIRD FLOOR!");
          return false;
        }
      } else {
        System.out.println("ERROR SECOND FLOOR!");
        return false;
      }
    } else {
      System.out.println("ERROR FIRST FLOOR!");
      return false;
    }

  }

  @Override
  public String testGetHash(String rawMatShpId) throws NoSuchAlgorithmException, JsonProcessingException {
    RawMaterialShipping rawMaterialShipping = rawMaterialShippingRepository.getReferenceById(rawMatShpId);
    rawMaterialShipping.setRmsCurrBlockHash(null);

    String jsonStr = new ObjectMapper().writeValueAsString(rawMaterialShipping);
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
    String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

    return encodedPtCurrBlockHash;
  }

  public String generateRawMaterialShippingId (long rawId) {
    String result = Long.toString(rawId);
    while (result.length() < 8) {
      result = "0" + result;
    }
    result = "RMS" + result;
    return result;
  }
}
