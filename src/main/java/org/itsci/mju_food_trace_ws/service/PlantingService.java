package org.itsci.mju_food_trace_ws.service;


import org.itsci.mju_food_trace_ws.model.Planting;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PlantingService {
    List<Planting> getAllPlanting();
    Planting getPlantingById(String plantingId);
    List<Planting> getListPlantingById(String farmerId);
    Planting savePlanting(Map<String, String> map) throws ParseException;
    Path downloadPlantingImg(String filePath);
    String uploadPlantingImg(MultipartFile file) throws IOException;
    boolean isPlantingImgExists (String plantingImg);
    Planting updatePlanting(Planting planting);
    void deletePlanting(String plantingId);
}
