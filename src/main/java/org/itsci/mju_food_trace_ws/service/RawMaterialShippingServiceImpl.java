package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
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

  @Override
  public RawMaterialShipping addRawMaterialShipping(Map<String, String> map) throws ParseException, NoSuchAlgorithmException, JsonProcessingException {

    String manuftId = map.get("manuftId");
    String plantingId = map.get("plantingId");

    DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    Date rawMatShpDate = format.parse(map.get("rawMatShpDate"));

    double rawMatShpQty = Double.parseDouble(map.get("rawMatShpQty"));
    String rawMatShpQtyUnit = map.get("rawMatShpQtyUnit");

    Planting planting = plantingRepository.getReferenceById(plantingId);

    //First step: query all rms which have plantingId equals to determined plantingId
    List<RawMaterialShipping> rmsContPtId = rawMaterialShippingRepository.getRawMaterialShippingsByPlanting_PlantingId(plantingId);

    double sumOfRmsGrams = 0;

    for (RawMaterialShipping rms : rmsContPtId) {
      if (rms.getRawMatShpQtyUnit().equals("กิโลกรัม")) {
        sumOfRmsGrams += rms.getRawMatShpQty() * 1000;
      } else {
        sumOfRmsGrams += rms.getRawMatShpQty();
      }
    }

    double ptNetQtyGrams = 0;

    if (planting.getNetQuantityUnit().equals("กิโลกรัม")) {
      ptNetQtyGrams = planting.getNetQuantity() * 1000;
    } else {
      ptNetQtyGrams = planting.getNetQuantity();
    }

    if (sumOfRmsGrams <= ptNetQtyGrams) {
      String jsonStr = new ObjectMapper().writeValueAsString(planting);
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
      String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash);

      planting.setPtCurrBlockHash(encodedPtCurrBlockHash);
      plantingRepository.save(planting);

      Manufacturer manufacturer = manufacturerRepository.getReferenceById(manuftId);

      String maxRawMatShpId = plantingRepository.getMaxPlantingId();
      long maxRmsLong = 0;

      if (maxRawMatShpId != null) {
        maxRmsLong = Long.parseLong(maxRawMatShpId.substring(2));
      }
      String rawMatShpId = generateRawMaterialShippingId(maxRmsLong + 1);

      RawMaterialShipping rawMaterialShipping = new RawMaterialShipping(rawMatShpId, rawMatShpDate, rawMatShpQty, rawMatShpQtyUnit, planting.getPtCurrBlockHash(), "", planting, manufacturer);

      String jsonStr2 = new ObjectMapper().writeValueAsString(rawMaterialShipping);
      MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
      byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
      String encodedRmsCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

      rawMaterialShipping.setRmsCurrBlockHash(encodedRmsCurrBlockHash);

      return rawMaterialShippingRepository.save(rawMaterialShipping);
    } else {
      return null;
    }

  }

  @Override
  public List<RawMaterialShipping> getListAllSentAgriByUsername(String username) {
    return rawMaterialShippingRepository.getRawMaterialShippingsByManufacturer_User_Username(username);
  }

  @Override
  public RawMaterialShipping getRawMaterialShippingById(String rawMatShpId) {
    return rawMaterialShippingRepository.getReferenceById(rawMatShpId);
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
