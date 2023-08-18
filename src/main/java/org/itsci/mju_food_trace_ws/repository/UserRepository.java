package org.itsci.mju_food_trace_ws.repository;

import org.itsci.mju_food_trace_ws.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    public User findUserByUsername (String username);

}
