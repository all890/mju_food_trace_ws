package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Farmer;
import org.itsci.mju_food_trace_ws.model.Manufacturer;
import org.itsci.mju_food_trace_ws.model.User;
import org.itsci.mju_food_trace_ws.repository.FarmerRepository;
import org.itsci.mju_food_trace_ws.repository.ManufacturerRepository;
import org.itsci.mju_food_trace_ws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Override
    public User saveUser(Map<String, String> map) {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User userLogin(Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        User user = userRepository.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            if ("FARMER".equals(user.getUserType())) {
                Farmer farmer = farmerRepository.getFarmerByUser_Username(user.getUsername());
                if ("รอการอนุมัติ".equals(farmer.getFarmerRegStatus())) {
                    user.setUserType("WAIT TO ACCEPT");
                } else if ("ไม่อนุมัติ".equals(farmer.getFarmerRegStatus())) {
                    user.setUserType("NOT ACCEPT");
                }
            } else if ("MANUFT".equals(user.getUserType())) {
                Manufacturer manufacturer = manufacturerRepository.getManufacturerByUser_Username(user.getUsername());
                if ("รอการอนุมัติ".equals(manufacturer.getManuftRegStatus())) {
                    user.setUserType("WAIT TO ACCEPT");
                } else if ("ไม่อนุมัติ".equals(manufacturer.getManuftRegStatus())) {
                    user.setUserType("NOT ACCEPT");
                }
            }
            return user;
        } else {
            return null;
        }
    }
}
