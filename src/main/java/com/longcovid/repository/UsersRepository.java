package com.longcovid.repository;

import com.longcovid.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);
    Boolean existsByUsername(String username);

    List<Users> findUsersByUsername(String name, Pageable pageable);
    List<Users> findByUsernameIn(List<String> username);

}
