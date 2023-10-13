package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.FarmerCertificate;
import org.itsci.mju_food_trace_ws.service.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/farmer")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    @RequestMapping("/add")
    public ResponseEntity addFarmer (@RequestBody Map<String, String> map) {
        try {
            Farmer farmer = farmerService.saveFarmer(map);
            return new ResponseEntity<>(farmer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save farmer data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/isfmmobileavailable/{farmerMobileNo}")
    public ResponseEntity isFarmerMobileNoAvailable (@PathVariable("farmerMobileNo") String farmerMobileNo) {
        try {
            Farmer farmer = farmerService.getFarmerByFarmerMobileNo(farmerMobileNo);
            if (farmer != null) {
                System.out.println("FM MOBILE NO WAS USED!");
                return new ResponseEntity<>("This farmer mobile no was used", HttpStatus.NOT_ACCEPTABLE);
            } else {
                System.out.println("FM MOBILE NO IS AVAILABLE!");
                return new ResponseEntity<>("This farmer mobile no is available", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get farmer's mobile no", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getfmdetails/{farmerId}")
    public ResponseEntity getFarmerDetails (@PathVariable("farmerId") String farmerId) {
        try {
            FarmerCertificate farmerCertificate = farmerService.getFarmerDetails(farmerId);
            return new ResponseEntity<>(farmerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get farmer's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listfmregist")
    public ResponseEntity getListAllFarmerRegistration () {
        try {
            List<Farmer> farmers = farmerService.getFarmersByRegistStat();
            return new ResponseEntity<>(farmers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list of farmer's registrations", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/updatefmregiststat/{farmerId}")
    public ResponseEntity updateFmRegistStatus (@PathVariable("farmerId") String farmerId) {
        try {
            System.out.println("UPDATE FARMER REGIST STAT");
            Farmer farmer = farmerService.updateFmRegistStatus(farmerId);
            return new ResponseEntity<>(farmer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update farmer registration status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/declinefmregiststat/{farmerId}")
    public ResponseEntity declineFmRegistStatus (@PathVariable("farmerId") String farmerId) {
        try {
            System.out.println("DECLINE FARMER REGIST STAT");
            Farmer farmer = farmerService.declineFmRegistStatus(farmerId);
            return new ResponseEntity<>(farmer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to decline farmer registration status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getfmbyusername/{username}")
    public ResponseEntity getFarmerUsername (@PathVariable("username") String username) {
        try {
            Farmer farmer = farmerService.getFarmerByUsername(username);
            return new ResponseEntity<>(farmer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get farmer's by username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
