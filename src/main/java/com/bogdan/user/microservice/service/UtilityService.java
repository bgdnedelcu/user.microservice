package com.bogdan.user.microservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class UtilityService {
    public String getEmailFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug(authentication.getName());
        return authentication.getName();
    }
}
