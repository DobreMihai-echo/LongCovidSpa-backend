package com.longcovid.security;


import com.longcovid.domain.Users;
import com.longcovid.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username: " + username + " not found. Register first!"));
        return new User(users.getUsername(), users.getPassword(), users.getEnabled(), users.isAccountNonExpired(), users.isCredentialsNonExpired(), users.isAccountNonLocked(), users.getAuthorities());
    }
}
