package com.longcovid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.longcovid.domain.Role;
import com.longcovid.domain.UserResponse;
import com.longcovid.domain.Users;
import com.longcovid.payload.SignupRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UsersService {

    Users registerUser(SignupRequest request) throws JsonProcessingException;

    Boolean existsByUsername(String username);

    String confirmToken(String token);


    Users saveUser(Users user);
    Role saveRole(Role role);

    Users getUser(String username);
    List<Users> getUsers();

    Users getUserById(Long id);

    Users updateUserProfile(MultipartFile profile, MultipartFile cover, String username, String firstName, String lastName, String email, String phone, String about) throws IOException;

    Users getUserByUsername(String username);
    List<UserResponse> getUserSearchResult(String key, Integer page, Integer size);

    UserResponse userToUserResponse(Users user);

    List<Users> getAllUsersByUsername(List<String> usernames);

    Users getAuthenticatedUser();

    Users changePermission(String roleName);


}
