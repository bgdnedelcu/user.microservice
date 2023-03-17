package com.bogdan.user.microservice.dao;

import com.bogdan.user.microservice.view.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsDao extends JpaRepository<Logs, Long> {
}