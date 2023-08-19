package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.repository.FarmerRepository;
import org.itsci.mju_food_trace_ws.repository.PlantingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PlantingServiceImpl implements PlantingService {
    final String PLANTING_IMG_FOLDER_PATH = "C:/img/planting/";
    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private PlantingRepository plantingRepository;

    @Override
    public List<Planting> getAllPlanting() {
        return plantingRepository.findAll();
    }

    @Override
    public Planting getPlantingById(String plantingId) {
        return plantingRepository.getReferenceById(plantingId);
    }

    @Override
    public List<Planting> getListPlantingById(String farmerId) {

        return plantingRepository.getPlantingsByFarmer_User_Username(farmerId);
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
