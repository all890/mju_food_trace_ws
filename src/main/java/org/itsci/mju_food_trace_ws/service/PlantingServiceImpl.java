package org.itsci.mju_food_trace_ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.itsci.mju_food_trace_ws.model.*;
import org.itsci.mju_food_trace_ws.repository.FarmerRepository;
import org.itsci.mju_food_trace_ws.repository.PlantingRepository;
import org.itsci.mju_food_trace_ws.repository.RawMaterialShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlantingServiceImpl implements PlantingService {
    final String PLANTING_IMG_FOLDER_PATH = "C:/img/planting/";
    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private PlantingRepository plantingRepository;

    @Autowired
    private RawMaterialShippingRepository rawMaterialShippingRepository;

    @Autowired
    private FarmerCertificateService farmerCertificateService;

    @Override
    public List<Planting> getAllPlanting() {
        return plantingRepository.findAll();
    }

    @Override
    public Planting getPlantingById(String plantingId) {
        return plantingRepository.getReferenceById(plantingId);
    }

    @Override
    public List<Planting> getListPlantingByFarmerUsername(String username) {

        return plantingRepository.getPlantingsByFarmerCertificate_Farmer_User_Username(username);
    }

    @Override
    public Planting savePlanting(Map<String, String> map) throws ParseException, JsonProcessingException, NoSuchAlgorithmException {
        Planting planting = null;

        String maxPlantingId = plantingRepository.getMaxPlantingId();
        long maxPlantingLong = 0;

        if (maxPlantingId != null) {
            maxPlantingLong = Long.parseLong(maxPlantingId.substring(2));
        }

        //Planting session
        String plantingId = generatePlantingId(maxPlantingLong + 1);
        String plantName = map.get("plantName");
        String plantDatestr = map.get("plantDate");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date plantDate = format.parse(plantDatestr);

        String plantingImg = map.get("plantingImg");
        String bioextract = map.get("bioextract");
        String approxHarvDatestr = map.get("approxHarvDate");
        Date approxHarvDate = format.parse(approxHarvDatestr);
        String plantingMethod = map.get("plantingMethod");
        double netQuantity = Double.parseDouble(map.get("netQuantity"));
        String netQuantityUnit = map.get("netQuantityUnit");
        double squareMeters = Double.parseDouble(map.get("squareMeters"));
        double squareYards = Double.parseDouble(map.get("squareYards"));
        double rai = Double.parseDouble(map.get("rai"));
        String username = map.get("username");

        FarmerCertificate farmerCertificate = farmerCertificateService.getLatestFarmerCertificateByFarmerUsername(username);

        planting = new Planting(plantingId,plantName,plantDate,plantingImg,bioextract,approxHarvDate,plantingMethod,netQuantity,netQuantityUnit,squareMeters,squareYards,rai, "0", null,farmerCertificate);

        User tempUser = planting.getFarmerCertificate().getFarmer().getUser();
        planting.getFarmerCertificate().getFarmer().setUser(null);

        String jsonStr2 = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));
        String encodedPtCurrBlockHash = Base64.getEncoder().encodeToString(hash2);

        planting.setPtCurrBlockHash(encodedPtCurrBlockHash);
        planting.getFarmerCertificate().getFarmer().setUser(tempUser);

        return plantingRepository.save(planting);
    }

    @Override
    public Path downloadPlantingImg(String filePath) {
        return new File(PLANTING_IMG_FOLDER_PATH + filePath).toPath();
    }

    @Override
    public String uploadPlantingImg(MultipartFile file) throws IOException {
        System.out.println("FILE NAME IS : " + file.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + ".png";
        file.transferTo(new File(PLANTING_IMG_FOLDER_PATH + newFileName));
        return newFileName;
    }

    @Override
    public boolean isPlantingImgExists(String plantingImg) {
        return plantingRepository.existsByPlantingImg(plantingImg);
    }

    @Override
    public Planting updatePlanting(Planting planting) {
        return plantingRepository.save(planting);
    }

    @Override
    public Map<String, Double> getRemainNetQtyOfPtsByFarmerUsername(String username) {
        Map<String, Double> remNetQty = new HashMap<>();
        List<Planting> plantings = plantingRepository.getPlantingsByFarmerCertificate_Farmer_User_Username(username);

        for (Planting planting : plantings) {
            double sumOfRawMatShpQtyGrams = 0.0;
            List<RawMaterialShipping> rawMaterialShipping = rawMaterialShippingRepository.getRawMaterialShippingsByPlanting_PlantingId(planting.getPlantingId());
            for (RawMaterialShipping rms : rawMaterialShipping) {
                if (rms.getRawMatShpQtyUnit().equals("กิโลกรัม")) {
                    sumOfRawMatShpQtyGrams += rms.getRawMatShpQty() * 1000.0;
                } else {
                    sumOfRawMatShpQtyGrams += rms.getRawMatShpQty();
                }
            }

            if (planting.getNetQuantityUnit().equals("กิโลกรัม")) {
                remNetQty.put(planting.getPlantingId(), (planting.getNetQuantity() * 1000.0) - sumOfRawMatShpQtyGrams);
            } else {
                remNetQty.put(planting.getPlantingId(), planting.getNetQuantity() - sumOfRawMatShpQtyGrams);
            }

        }

        return remNetQty;
    }

    @Override
    public void deletePlanting(String plantingId) {
        Planting planting = plantingRepository.getReferenceById(plantingId);
        planting.setFarmerCertificate(null);
        plantingRepository.delete(planting);
    }

    @Override
    public String testGetNewPtCurrBlockHash(String plantingId) throws JsonProcessingException, NoSuchAlgorithmException {
        Planting planting = plantingRepository.getReferenceById(plantingId);
        planting.getFarmerCertificate().getFarmer().setUser(null);
        planting.setPtCurrBlockHash(null);

        String jsonStr2 = new ObjectMapper().writeValueAsString(planting);
        MessageDigest digest2 = MessageDigest.getInstance("SHA-256");
        byte[] hash2 = digest2.digest(jsonStr2.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash2);
    }

    public String generatePlantingId (long rawId) {
        String result = Long.toString(rawId);
        while (result.length() < 8) {
            result = "0" + result;
        }
        result = "PT" + result;
        return result;
    }
}
