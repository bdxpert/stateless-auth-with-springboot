package com.stateless.service.services;

import com.stateless.service.entities.User;

import java.io.IOException;

public interface UserService {

	User create(User user) throws Exception;
	User findByAccessToken(String accessToken) throws IOException;
	User update(User user) throws Exception;
	User findByUsername(String username) throws Exception;
}
