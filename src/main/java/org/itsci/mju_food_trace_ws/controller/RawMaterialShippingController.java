package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.service.PlantingService;
import org.itsci.mju_food_trace_ws.service.RawMaterialShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rms")
public class RawMaterialShippingController {

    @Autowired
    private RawMaterialShippingService rawMaterialShippingService;

    @PostMapping("/add")
    public ResponseEntity addRawMaterialShipping(@RequestBody Map<String, String> map) {
        try {
            RawMaterialShipping rawMaterialShipping = rawMaterialShippingService.addRawMaterialShipping(map);
            if (rawMaterialShipping != null) {
                return new ResponseEntity<>(rawMaterialShipping, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add rms because sum rawMatShpQty greater than plantingNetQty", HttpStatus.resolve(480));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to add raw material shipping", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listallsentagri/{username}")
    public ResponseEntity getListAllSentAgri(@PathVariable("username") String username) {

        try {
            List<RawMaterialShipping> rawMaterialShippings = rawMaterialShippingService.getListAllSentAgriByUsername(username);
            return new ResponseEntity<>(rawMaterialShippings, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list all sent agricultural products", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getrmsdetails/{rawMatShpId}")
    public ResponseEntity getRawMaterialShippingDetails(@PathVariable("rawMatShpId") String rawMatShpId) {
        try {
            RawMaterialShipping rawMaterialShipping = rawMaterialShippingService.getRawMaterialShippingById(rawMatShpId);
            return new ResponseEntity<>(rawMaterialShipping, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get planting's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/testgethash/{rawMatShpId}")
    public ResponseEntity testGetHash (@PathVariable("rawMatShpId") String rawMatShpId) {
        try {
            String hash = rawMaterialShippingService.testGetHash(rawMatShpId);
            return new ResponseEntity<>(hash, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get hash", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
