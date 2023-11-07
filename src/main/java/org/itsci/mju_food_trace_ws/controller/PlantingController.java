package org.itsci.mju_food_trace_ws.controller;


import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.service.PlantingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planting")
public class PlantingController {
    @Autowired
    private PlantingService plantingService;

    @RequestMapping("/add")
    public ResponseEntity addPlanting(@RequestBody Map<String, String> map) {
        try {
            Planting planting = plantingService.savePlanting(map);
            return new ResponseEntity<>(planting, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save planting data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getremqtyofpts/{username}")
    public ResponseEntity getRemQtyOfPtsByFarmerUsername (@PathVariable("username") String username) {
        try {
            Map<String, Double> remQtyOfPts = plantingService.getRemainNetQtyOfPtsByFarmerUsername(username);
            return new ResponseEntity<>(remQtyOfPts, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list of remaining qty of plantings.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/uploadimg")
    public ResponseEntity uploadPlantingImg(@RequestParam("image") MultipartFile file) throws IllegalStateException, IOException {
        try {
            String filePath = plantingService.uploadPlantingImg(file);
            return new ResponseEntity<>(filePath, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/{filePath}")
    public byte[] downloadPlantingImg (@PathVariable("filePath") String filePath) throws IOException {
        byte[] image = Files.readAllBytes(plantingService.downloadPlantingImg(filePath));
        return image;
    }

    @GetMapping("/ischainbefptval/{username}")
    public ResponseEntity isChainBeforePlantingValid (@PathVariable("username") String username) {
        try {
            boolean isChainValid = plantingService.isChainBeforePlantingValid(username);
            if (isChainValid) {
                return new ResponseEntity<>("Chain before planting is valid", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Chain before planting isn't valid", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get planting's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listplantings/{username}")
    public ResponseEntity getListPlantingByFarmerUsername (@PathVariable("username") String username){
        try {
            List<Planting> plantingList = plantingService.getListPlantingByFarmerUsername(username);
            return new ResponseEntity<>(plantingList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list of planting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getplantings/{plantingId}")
    public ResponseEntity getFarmerDetails (@PathVariable("plantingId") String plantingId) {
        try {
            Planting planting = plantingService.getPlantingById(plantingId);
            return new ResponseEntity<>(planting, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get planting's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/isimgexist")
    public ResponseEntity isPlantingImgExists (@RequestBody Map<String, String> map) {
        try {
            String imgPath = map.get("imgPath");
            boolean imgBool = plantingService.isPlantingImgExists(imgPath);
            if (imgBool) {
                return new ResponseEntity<>(true, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get planting's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/update")
    public ResponseEntity updatePlanting(@RequestBody Planting planting){
        try{
            Planting updatePlanting = plantingService.updatePlanting(planting);
            return new ResponseEntity<>(updatePlanting, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update planting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/delete/{plantingId}")
    public  ResponseEntity deletePlanting(@PathVariable("plantingId") String plantingId){
        try {
            plantingService.deletePlanting(plantingId);
            return new ResponseEntity<>("Delete Planting succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to delete planting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/testgetptcurrblockhash/{plantingId}")
    public ResponseEntity getNewPtCurrBlockHash (@PathVariable("plantingId") String plantingId) {
        try {
            String newPtCurrBlockHash = plantingService.testGetNewPtCurrBlockHash(plantingId);
            return new ResponseEntity<>(newPtCurrBlockHash, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get pt curr block hash", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
