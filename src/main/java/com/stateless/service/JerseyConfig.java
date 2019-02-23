package com.stateless.service;

import com.stateless.service.controllers.UserController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

/**
 * @author Sanjniv @ sanjiv.sarkar@itconquest.com
 */
@Configuration
@ApplicationPath("/api/v1")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {


        register(UserController.class);



    }
}