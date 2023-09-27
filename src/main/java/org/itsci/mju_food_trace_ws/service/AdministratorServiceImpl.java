package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Administrator;
import org.itsci.mju_food_trace_ws.repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Override
    public Administrator getAdminByUsername(String username) {
        return administratorRepository.getAdministratorByUser_Username(username);
    }
}
