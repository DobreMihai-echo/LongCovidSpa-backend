package com.longcovid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    String username;
    String toEmail;
    String toPhone;
    String message;
    String token;
    String subject;
}
