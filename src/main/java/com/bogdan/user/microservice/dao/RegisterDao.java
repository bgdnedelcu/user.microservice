package com.bogdan.user.microservice.dao;

import com.bogdan.user.microservice.view.RegisterCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisterDao extends JpaRepository<RegisterCode, Long> {

   RegisterCode findByRegisterKey(final String registerKey);

   Optional<RegisterCode> findById(final Long id);

}
