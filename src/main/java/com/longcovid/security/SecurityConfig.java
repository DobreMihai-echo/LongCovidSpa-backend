package com.longcovid.security;

import com.longcovid.configuration.JWTAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.longcovid.domain.ERole.*;
import static com.longcovid.domain.Permission.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private JWTAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/footprint/**").hasAnyRole(USER.name(),ORGANIZATION.name())
                .antMatchers("/api/user/**").hasAnyRole(USER.name(),ORGANIZATION.name())
                .antMatchers("/api/organization/join").hasAnyRole(USER.name())

                .antMatchers(HttpMethod.GET,"/api/organization/**").hasAnyRole(ORGANIZATION.name(),USER.name(),ORGANIZATION_MANAGER.name(),ORGANIZATION_CREATE.name(),ORGANIZATION_UPDATE.name(),ORGANIZATION_DELETE.name())
                .antMatchers(HttpMethod.POST,"/api/organization/**").hasAnyAuthority(ORGANIZATION.name(),ORGANIZATION_MANAGER.name())
                .antMatchers(HttpMethod.DELETE,"/api/organization/**").hasAnyAuthority(ORGANIZATION.name(),ORGANIZATION_MANAGER.name())
                .antMatchers("/api/organization/**").hasAnyRole(ORGANIZATION.name())
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

   @Autowired
   private UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTRequestFilter jwtRequestFilter() {
        return new JWTRequestFilter();
    }
}
