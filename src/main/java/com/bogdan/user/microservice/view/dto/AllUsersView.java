package com.bogdan.user.microservice.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllUsersView {

    private Long id;
    private String email;
    private String role;
    private String channelName;

}
