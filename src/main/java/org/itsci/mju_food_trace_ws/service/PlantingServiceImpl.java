package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.repository.FarmerRepository;
import org.itsci.mju_food_trace_ws.repository.PlantingRepository;
import org.itsci.mju_food_trace_ws.repository.RawMaterialShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

        return plantingRepository.getPlantingsByFarmer_User_Username(username);
    }

    @Override
    public Planting savePlanting(Map<String, String> map) throws ParseException {
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
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date plantDate = format.parse(plantDatestr);

        String plantingImg = map.get("plantingImg");
        String bioextract = map.get("bioextract");
        String approxHarvDatestr = map.get("approxHarvDate");
        Date approxHarvDate = format.parse(approxHarvDatestr);
        String plantingMethod = map.get("plantingMethod");
        double netQuantity = Double.parseDouble(map.get("netQuantity"));
        String netQuantityUnit = map.get("netQuantityUnit");
        int squareMeters = Integer.parseInt(map.get("squareMeters"));
        int squareYards = Integer.parseInt(map.get("squareYards"));
        int rai = Integer.parseInt(map.get("rai"));
        String username = map.get("username");
        String ptPrevBlockHash = "0";
        String ptCurrBlockHash = map.get("ptCurrBlockHash");

        Farmer farmer = farmerRepository.getFarmerByUser_Username(username);

        planting = new Planting(plantingId,plantName,plantDate,plantingImg,bioextract,approxHarvDate,plantingMethod,netQuantity,netQuantityUnit,squareMeters,squareYards,rai, ptPrevBlockHash, ptCurrBlockHash,farmer);
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
        return PLANTING_IMG_FOLDER_PATH + newFileName;
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
        List<Planting> plantings = plantingRepository.getPlantingsByFarmer_User_Username(username);

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
        planting.setFarmer(null);
        plantingRepository.delete(planting);
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
