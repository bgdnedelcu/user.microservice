package com.bogdan.user.microservice.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Entity
@Table(name = "register_codes")
public class RegisterCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "register_key")
    private String registerKey;

    @Column(name = "used")
    private Integer used;

    @Column(name = "generated_date")
    private Date date;

    @OneToOne(mappedBy = "registerCode", fetch = FetchType.LAZY)
    private User user;

}
