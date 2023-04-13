package com.bogdan.user.microservice.dao;

import com.bogdan.user.microservice.view.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayListDao extends JpaRepository<PlayList, Long> {

    Optional<PlayList> findById(Long id);

}