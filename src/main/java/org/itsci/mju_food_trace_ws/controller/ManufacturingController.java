package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.service.ManufacturingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manufacturing")
public class ManufacturingController {
    @Autowired
    private ManufacturingService manufacturingService;

    @RequestMapping("/add")
    public ResponseEntity addManufacturing (@RequestBody Map<String, String> map) {
        try {
            Manufacturing manufacturing = manufacturingService.addManufacturing(map);
            return new ResponseEntity<>(manufacturing, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save manufacturing data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/record/{manufacturingId}")
    public ResponseEntity recordManufacturing(@PathVariable("manufacturingId") String manufacturingId) {
        try {
            Manufacturing manufacturing = manufacturingService.recordManufacturing(manufacturingId);
            if (manufacturing != null) {
                return new ResponseEntity<>(manufacturing, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to save manufacturing data because new encryption code isn't match as original", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save manufacturing data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listallmanufacturing/{username}")
    public ResponseEntity getListAllManufacturing(@PathVariable("username") String username) {
        try {
            List<Manufacturing> manufacturings = manufacturingService.getListAllManufacturingByUsername(username);
            return new ResponseEntity<>(manufacturings, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list all manufacturings", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getmanufacturungs/{manufacturingId}")
    public ResponseEntity getManufacturingById (@PathVariable("manufacturingId") String manufacturingId) {
        try {
            Manufacturing manufacturing = manufacturingService.getManufacturingById(manufacturingId);
            return new ResponseEntity<>(manufacturing, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get manufacturing's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping("/update")
    public ResponseEntity updateManufacturing(@RequestBody Manufacturing manufacturing){
        try{
            Manufacturing updateManufacturing = manufacturingService.updateManufacturing(manufacturing);
            return new ResponseEntity<>(updateManufacturing, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update planting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping("/delete/{manufacturingId}")
    public  ResponseEntity deletePlanting(@PathVariable("manufacturingId") String manufacturingId){
        try {
            manufacturingService.deleteManufacturing(manufacturingId);
            return new ResponseEntity<>("Delete manufacturing succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to delete planting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
