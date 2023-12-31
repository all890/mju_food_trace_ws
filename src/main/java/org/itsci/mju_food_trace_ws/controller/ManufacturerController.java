package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.ManufacturerCertificate;
import org.itsci.mju_food_trace_ws.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manuft")
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    @RequestMapping("/add")
    public ResponseEntity addManufacturer (@RequestBody Map<String, String> map) {
        try {
            Manufacturer manufacturer = manufacturerService.saveManufacturer(map);
            return new ResponseEntity<>(manufacturer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save manufacturer data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getmndetails/{manuftId}")
    public ResponseEntity getManufacturerDetails (@PathVariable("manuftId") String manuftId) {
        try {
            ManufacturerCertificate manufacturerCertificate = manufacturerService.getManufacturerDetails(manuftId);
            return new ResponseEntity<>(manufacturerCertificate, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get manufacturer's details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/updatemnregiststat/{manuftId}")
    public ResponseEntity updateMnRegistStatus (@PathVariable("manuftId") String manuftId) {
        try {
            System.out.println("UPDATE MANUFT REGIST STAT");
            Manufacturer manufacturer = manufacturerService.updateMnRegistStatus(manuftId);
            return new ResponseEntity<>(manufacturer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update manufacturer registration status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/declinemnregiststat/{manuftId}")
    public ResponseEntity declineMnRegistStatus (@PathVariable("manuftId") String manuftId) {
        try {
            System.out.println("DECLINE MANUFT REGIST STAT");
            Manufacturer manufacturer = manufacturerService.declineMnRegistStatus(manuftId);
            return new ResponseEntity<>(manufacturer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to decline manufacturer registration status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listmnregist")
    public ResponseEntity getListAllManufacturerRegistration () {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturersByRegistStat();
            return new ResponseEntity<>(manufacturers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list of manufacturer's registrations", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping("/list")
    public ResponseEntity getListAllManufacturer () {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
            return new ResponseEntity<>(manufacturers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get list manufacturer's ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getmnbyusername/{username}")
    public ResponseEntity getManufacturerUsername (@PathVariable("username") String username) {
        try {
            Manufacturer manufacturer = manufacturerService.getManufacturerByUsername(username);
            return new ResponseEntity<>(manufacturer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get farmer's by username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/ismnnameavailable/{manuftName}")
    public ResponseEntity isManuftNameAvailable (@PathVariable("manuftName") String manuftName) {
        try {
            if (manufacturerService.isManufacturerAvailable(manuftName)) {
                System.out.println("Can't use this manuft name because not available");
                return new ResponseEntity<>("Can't use this manuft name because not available", HttpStatus.NOT_ACCEPTABLE);
            } else {
                System.out.println("Manuft name is available");
                return new ResponseEntity<>("Manuft name is available", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get manuft by name", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ismanuftavailable/{manuftName}")
    public ResponseEntity isManufacturerAvailable (@PathVariable("manuftName") String manuftName) {
        try {
            System.out.println(manuftName);
            if (manufacturerService.isManufacturerAvailable(manuftName)) {
                return new ResponseEntity<>("Found manufacturer by manuftname", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not found manufacturer by manuftname", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to get manufacturer by manuftname", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
