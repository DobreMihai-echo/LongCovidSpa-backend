package com.longcovid.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SignupRequest {

    private final String firstName;
    private final String lastName;
    private final String organizationName;
    private final String email;
    private final String password;
    private final String username;
    private final String phone;
    private final Boolean isOrganization;
    private final String country;
}
