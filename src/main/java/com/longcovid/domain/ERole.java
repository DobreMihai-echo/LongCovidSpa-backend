package com.longcovid.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum ERole {
    USER(Collections.emptySet()),
    ORGANIZATION(
            Set.of(Permission.ORGANIZATION_READ,Permission.ORGANIZATION_UPDATE,Permission.ORGANIZATION_DELETE,Permission.ORGANIZATION_CREATE)
    ),
    ORGANIZATION_USER(
            Set.of(Permission.ORGANIZATION_READ)
    ),
    ORGANIZATION_MANAGER(
            Set.of(Permission.ORGANIZATION_READ,Permission.ORGANIZATION_CREATE)
    ),
    ORGANIZATION_ADMIN(
            Set.of(Permission.ORGANIZATION_READ,Permission.ORGANIZATION_CREATE,Permission.ORGANIZATION_UPDATE)
    ),
    ROLE_ADMIN(Collections.emptySet());

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    };

}

