package com.longcovid.service.impl;

import com.longcovid.domain.ConfirmationToken;
import com.longcovid.repository.ConfirmationTokenRepository;
import com.longcovid.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;


    public ConfirmationToken getToken(String token) {
        return repository.findByToken(token).orElseThrow();
    }

    public void generateConfirmationToken(ConfirmationToken token) {
        repository.save(token);
    }

    @Override
    public void setConfirmedAt(String token) {
        ConfirmationToken tokenToConfirm = repository.findByToken(token).orElseThrow();
        tokenToConfirm.setConfirmedAt(LocalDateTime.now());
        repository.save(tokenToConfirm);
    }
}
