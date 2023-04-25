package com.bogdan.user.microservice.view.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditAccount {

    private String currentPassword;
    private String newPassword;
    private String newChannelName;

}
