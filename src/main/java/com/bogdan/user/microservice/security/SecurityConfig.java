package com.bogdan.user.microservice.security;

import com.bogdan.user.microservice.filter.MyAuthenticationFilter;
import com.bogdan.user.microservice.filter.MyAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private UserDetailsService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AuthenticationManager authenticationManager;

    @Autowired
    public SecurityConfig(final UserDetailsService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final MyAuthenticationFilter myAuthenticationFilter = new MyAuthenticationFilter(authenticationManager);
        myAuthenticationFilter.setFilterProcessesUrl("/videoplatform/api/account/login");
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                .requestMatchers("/videoplatform/api/account/register").permitAll()
                .requestMatchers("/videoplatform/api/account/login").permitAll()
                .requestMatchers("/videoplatform/api/account/finishregistration/**").permitAll()
                .requestMatchers("/videoplatform/api/account/getIdByEmail").permitAll()
                .requestMatchers("/videoplatform/api/account/channelNameById/*").permitAll()
                .requestMatchers("/videoplatform/api/account/getIdByChannelName/**").permitAll()
                .anyRequest().authenticated();
        http.addFilter(myAuthenticationFilter);
        http.addFilterBefore(new MyAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
