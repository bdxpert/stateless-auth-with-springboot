package com.stateless.service.security;

import com.stateless.service.services.UserService;
import com.stateless.service.extra.AppConstant;
import com.stateless.service.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public final class TokenHandler {

    @Autowired
    private UserService userService;

    public User parseUserFromToken(String token) throws IOException {
        User user = userService.findByAccessToken(token);
        if (user != null && user.isResourceAccessTokenActive() && user.isEnabled() ) {
//            updateTokenExpiryTime(user);
            user.setAuthorities(AppConstant.ROLE_USER);

            return user;
        }

        return null;
    }

    /*private User updateTokenExpiryTime(User user) {
        LocalDateTime storedAccessTokenExpiryTime = user.getAccessTokenExpiryTime().plusMinutes(2);
        LocalDateTime newAccessTokenExpiryTime = LocalDateTime.now().plusMinutes(AppConstant.ACCESS_TOKEN_EXPIRED_MINUTES);
        if(newAccessTokenExpiryTime.isAfter(storedAccessTokenExpiryTime)) { // update access token after 1 minute
            user.setAccessTokenExpiryTime(newAccessTokenExpiryTime);
            userService.update(user);
        }
        return user;
    }*/

}
