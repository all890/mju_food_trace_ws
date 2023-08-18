package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.Planting;
import org.itsci.mju_food_trace_ws.model.RawMaterialShipping;
import org.itsci.mju_food_trace_ws.service.PlantingService;
import org.itsci.mju_food_trace_ws.service.RawMaterialShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rms")
public class RawMaterialShippingController {

    @Autowired
    private RawMaterialShippingService rawMaterialShippingService;

    @RequestMapping("/listallsentagri/{username}")
    public ResponseEntity getListAllSentAgri (@PathVariable("username") String username) {

        try {
            List<RawMaterialShipping> rawMaterialShippings = rawMaterialShippingService.getListAllSentAgriByUsername(username);
            return new ResponseEntity<>(rawMaterialShippings, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list all sent agricultural products", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
