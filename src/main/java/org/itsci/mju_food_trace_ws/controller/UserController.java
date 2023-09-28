package org.itsci.mju_food_trace_ws.controller;

import org.itsci.mju_food_trace_ws.model.User;
import org.itsci.mju_food_trace_ws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/iua")
    public ResponseEntity isUsernameAvailable (@RequestBody Map<String, String> map) {
        String username = map.get("username");
        if (userService.findUserByUsername(username) == null) {
            System.out.println("USERNAME IS AVAILABLE!");
            return new ResponseEntity<>("Username is available.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Username is already exists!", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping("/login")
    public ResponseEntity userLogin (@RequestBody Map<String, String> map) {
        try {
            User user = userService.userLogin(map);
            if (user != null) {
                if ("WAIT TO ACCEPT".equals(user.getUserType())) {
                    return new ResponseEntity<>("This user is waiting for accepting from administrator", HttpStatus.BAD_REQUEST);
                } else if ("NOT ACCEPT".equals(user.getUserType())) {
                    return new ResponseEntity<>("This user was rejected from administrator", HttpStatus.CONFLICT);
                } else {
                    return new ResponseEntity<>(user, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("This user wasn't found in the database", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to login, please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
