package com.longcovid.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ORGANIZATION_READ("organization:read"),
    ORGANIZATION_UPDATE("organization:update"),
    ORGANIZATION_CREATE("organization:create"),
    ORGANIZATION_DELETE("organization:delete");


    @Getter
    private final String permission;
}
