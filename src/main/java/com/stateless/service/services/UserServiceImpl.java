package com.stateless.service.services;

import com.stateless.service.entities.User;
import com.stateless.service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public User create(User user) {
		repository.save(user);

		log.info("new user has been created: {}", user.getUsername());
		return user;
	}

	@Override
	public User findByAccessToken(String accessToken) {
		Query query = new Query();
		query.addCriteria(Criteria.where("accessToken").is(accessToken));

		return mongoTemplate.findOne(query, User.class);
	}

	@Override
	public User update(User user) {
		return repository.save(user);
	}

	@Override
	public User findByUsername(String username) {

		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(username));

		return mongoTemplate.findOne(query, User.class);
	}


}
