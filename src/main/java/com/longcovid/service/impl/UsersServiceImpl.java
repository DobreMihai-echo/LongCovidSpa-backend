package com.longcovid.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longcovid.domain.*;
import com.longcovid.payload.SignupRequest;
import com.longcovid.repository.RolesRepository;
import com.longcovid.repository.UsersRepository;
import com.longcovid.service.ConfirmationTokenService;
import com.longcovid.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository userRepository;
    private final RolesRepository rolesRepository;
    private final ConfirmationTokenService confirmationTokenService;

    private final PasswordEncoder encoder;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${application.link}")
    private String applicationLink;

    @Override
    @Transactional
    public Users registerUser(SignupRequest request) throws JsonProcessingException {
        boolean userExists = userRepository.findByUsername(request.getUsername()).isPresent();

        if (userExists) {
            throw new IllegalStateException("Username already taken");
        }
        List<Role> userRoles = new ArrayList<>();
        String encodedPassword = encoder.encode(request.getPassword());
        userRoles.add(new Role(ERole.USER));
        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .roles(userRoles)
                .enabled(false)
                .build();
        userRepository.save((Users) user);



        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .confirmedAt(null)
                .user(user)
                .build();

        confirmationTokenService.generateConfirmationToken(confirmationToken);

        String link = applicationLink + "/api/auth/confirm?token=" + token;
        Message message = Message.builder()
                .toEmail(user.getEmail())
                .username(user.getUsername())
                .toPhone(user.getPhone())
                .message("Welcome to Long Covid Application")
                .subject("Activate account")
                .token(link)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(message);
        kafkaTemplate.send("longcovvid_notification_topic", jsonString);
        return user;
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        enableAccount(confirmationToken.getUser().getUsername());

        return "confirmed";

    }

    private void enableAccount(String username) {
        Users users = userRepository.findByUsername(username).orElseThrow();
        users.setEnabled(true);
        userRepository.save(users);
    }

    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return rolesRepository.save(role);
    }

    @Override
    public Users getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Users updateUserProfile(MultipartFile profile,MultipartFile cover, String username, String firstName, String lastName, String email, String phone, String about) throws IOException {
        Users userToUpdate = this.getAuthenticatedUser();
        userToUpdate.setProfilePhoto(profile.getBytes());
        userToUpdate.setPhone(phone);
        userToUpdate.setUsername(username);
        userToUpdate.setFirstName(firstName);
        userToUpdate.setLastName(lastName);
        userToUpdate.setEmail(email);
        return userRepository.save(userToUpdate);
    }

    @Override
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }


    @Override
    public List<UserResponse> getUserSearchResult(String key, Integer page, Integer size) {
        return userRepository.findUsersByUsername(
                key,
                PageRequest.of(page, size)
        ).stream().map(this::userToUserResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse userToUserResponse(Users user) {
        return UserResponse.builder()
                .user(user)
                .build();
    }

    @Override
    public List<Users> getAllUsersByUsername(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    public Users getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String authUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<Users> optionalUsers = userRepository.findByUsername(authUsername);
            if (optionalUsers.isPresent()) {
                return optionalUsers.get();
            } else {
                System.out.println("ELSE 1");
            }
        } else {
            System.out.println("ELSE 2");
        }
        System.out.println("WORSE");
        return null;
    }

    @Override
    public Users changePermission(String roleName) {
        return null;
    }


    


}
