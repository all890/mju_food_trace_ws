package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.Administrator;
import org.itsci.mju_food_trace_ws.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administrator")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @GetMapping("/getadminbyusername/{username}")
    public ResponseEntity getAdminByUsername (@PathVariable("username") String username) {
        try {
            Administrator administrator = administratorService.getAdminByUsername(username);
            return new ResponseEntity<>(administrator, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to find administrator by username!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
