package com.longcovid.repository;

import com.longcovid.domain.ERole;
import com.longcovid.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role,Long> {
    Role findByName(ERole name);
    Boolean existsByName(ERole name);
}
