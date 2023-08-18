package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Manufacturing;
import org.itsci.mju_food_trace_ws.service.ManufacturingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
