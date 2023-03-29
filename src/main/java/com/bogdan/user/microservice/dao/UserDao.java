package com.bogdan.user.microservice.dao;


import com.bogdan.user.microservice.view.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserDao extends JpaRepository<User, Long> {

    List<User> findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    Optional<User> findFirstIdByEmail(String email);

}
