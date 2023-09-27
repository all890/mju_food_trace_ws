package org.itsci.mju_food_trace_ws.service;

import org.itsci.mju_food_trace_ws.model.Administrator;

public interface AdministratorService {

    Administrator getAdminByUsername(String username);

}
