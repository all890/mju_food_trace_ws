package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.User;
import org.itsci.mju_food_trace_ws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
            return user;
        } else {
            return null;
        }
    }
}
