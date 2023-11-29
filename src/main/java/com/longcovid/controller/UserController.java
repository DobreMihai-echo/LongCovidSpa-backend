package com.longcovid.controller;

import com.longcovid.domain.Role;
import com.longcovid.domain.UserResponse;
import com.longcovid.domain.Users;
import com.longcovid.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UsersService service;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getUsers() {
        return ResponseEntity.ok(service.getUsers());
    }

    @PostMapping("/user")
    public ResponseEntity<Users> saveUser(@RequestBody Users usersBase){
        return ResponseEntity.ok().body(service.saveUser(usersBase));
    }

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        return ResponseEntity.ok().body(service.saveRole(role));
    }

    @GetMapping("/user")
    public ResponseEntity<Users> getUser(@RequestParam(name = "username") String username) {
        return ResponseEntity.ok().body(service.getUser(username));
    }

    @GetMapping("/userid")
    public ResponseEntity<?> getUserById(@RequestParam(name = "userId") Long userId) {
        Users targetUser = service.getUserById(userId);
        UserResponse userResponse = UserResponse.builder()
                .user(targetUser)
                .build();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUser(@RequestParam("key") String key,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size) {
        page = page < 0 ? 0 : page-1;
        size = size <= 0 ? 5 : size;
        List<UserResponse> userSearchResult = service.getUserSearchResult(key, page, size);
        return new ResponseEntity<>(userSearchResult, HttpStatus.OK);
    }

    @PutMapping(value = "/user/profile", consumes = "multipart/form-data")
    public ResponseEntity<?> editProfile(@RequestParam(name = "profilePicture")MultipartFile profile, @RequestParam(name = "coverPicture") MultipartFile cover,@RequestParam(name = "username") String username, @RequestParam(name = "firstName") String firstName, @RequestParam(name = "lastName") String lastName, @RequestParam(name = "email") String email,@RequestParam(name = "phone") String phone , @RequestParam(name = "about") String about) {
        try {
            return ResponseEntity.ok(service.updateUserProfile(profile,cover,username,firstName,lastName,email,phone,about));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("An error occured:" + ex.getMessage());
        }
    }

}
