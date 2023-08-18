package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.User;

import java.util.Map;

public interface UserService {

    User saveUser(Map<String, String> map);
    User findUserByUsername(String username);
    User userLogin(Map<String, String> map);

}
