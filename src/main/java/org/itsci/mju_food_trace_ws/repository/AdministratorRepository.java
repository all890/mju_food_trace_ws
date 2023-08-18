package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, String> {
}
