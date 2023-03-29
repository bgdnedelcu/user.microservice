package com.bogdan.user.microservice.dao;

import com.bogdan.user.microservice.view.RegisterCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterDao extends JpaRepository<RegisterCode, Long> {

   RegisterCode findByRegisterKey(final String registerKey);

}
