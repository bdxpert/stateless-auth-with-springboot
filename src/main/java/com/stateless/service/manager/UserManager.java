package com.stateless.service.manager;

import com.stateless.service.entities.User;
import com.stateless.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by sanjiv on 2/15/17.
 */
@Component
public class UserManager {
    @Autowired
    private UserService userService;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User create(User user) throws Exception{
        if(user.getAge() < 30 || user.getAge() > 50) {
            throw new Exception("User age must be between 30 to 50 years");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedOn(LocalDateTime.now());

        return userService.create(user);
    }

    public User update(User user) throws Exception{

        user.setUpdatedOn(LocalDateTime.now());

        return userService.update(user);
    }
    private String createToken(User user) throws Exception {
        String hash = encoder.encode(user.getEmail()+LocalDateTime.now().toString());

        final StringBuilder sb = new StringBuilder(170);
        sb.append(hash);
        sb.append(".");
        sb.append(LocalDateTime.now());
        return sb.toString();
    }

    public User authenticate(String username, String password) throws Exception{
        User user = userService.findByUsername(username);

        if (user == null) {
            return null;
        } else if(!encoder.matches(password, user.getPassword())) {
            return null;
        } else if(user.getAccessTokenExpiryTime() == null) {
            user.setAccessToken(createToken(user));
            user.setAccessTokenCreationTime(LocalDateTime.now());
            user.setLoginDateTime(LocalDateTime.now());
        } else if (user.getAccessTokenExpiryTime() !=null && user.getAccessTokenExpiryTime().isBefore(LocalDateTime.now())) {
            // set accessToken and
            user.setAccessToken(createToken(user));
            user.setAccessTokenCreationTime(LocalDateTime.now());
            user.setLoginDateTime(LocalDateTime.now());
        }
        user.setAuthorities("ROLE_USER");
        user.setAccessTokenExpiryTime(LocalDateTime.now().plusMinutes(60));
        update(user); // update user details;

        return user;
    }
}
