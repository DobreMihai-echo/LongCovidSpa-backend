package com.longcovid.service;

import com.longcovid.domain.ConfirmationToken;

public interface ConfirmationTokenService {

    ConfirmationToken getToken(String token);
    void generateConfirmationToken(ConfirmationToken token);

    void setConfirmedAt(String token);
}
