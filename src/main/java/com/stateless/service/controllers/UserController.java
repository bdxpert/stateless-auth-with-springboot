package com.stateless.service.controllers;

import com.stateless.service.SecurityConfig;
import com.stateless.service.entities.User;
import com.stateless.service.exceptions.AppException;
import com.stateless.service.extra.UserLogin;
import com.stateless.service.manager.ErrorResponseManager;
import com.stateless.service.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanjiv on 2/15/17.
 */
@Controller
@Path("/users")
public class UserController {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserManager userManager;
    @Autowired
    private SecurityConfig securityConfig;

    @POST
    @Path("/register")
    @Consumes("application/json")
    @Produces("application/json")
    public Response userRegisterAction(User user, @Context HttpServletRequest requestContext) throws AppException, Exception {

        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        Set<ConstraintViolation<Object>> validateErrors = validator.validate(user);
        if (validateErrors.isEmpty()) {
            serviceResponse.put("user", userManager.create(user));

            return Response.status(HttpServletResponse.SC_OK).entity(serviceResponse).build();
        } else {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(ErrorResponseManager.buildValidationErrors(validateErrors)).build();
        }
    }
    /*******************************
     * user login
     * EndPoint:api/v1/users/login
     **********************************/
    @POST
    @Path("/login")
    @Consumes("application/json")
    @Produces("application/json")
    public Response userLoginAction(UserLogin userLogin, @Context HttpServletRequest requestContext) throws AppException, Exception {

        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        Set<ConstraintViolation<Object>> validateErrors = validator.validate(userLogin);
        if (validateErrors.isEmpty()) {
            // Set access token for the user
            AuthenticationManager authMgr = securityConfig.authenticationManagerBean();
            UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword());
            authReq.setDetails(new WebAuthenticationDetailsSource().buildDetails(requestContext));
            Authentication authResp = authMgr.authenticate(authReq);

            User user = (User) authResp.getPrincipal();

            serviceResponse.put("user", user);

            return Response.status(HttpServletResponse.SC_OK).entity(serviceResponse).build();
        } else {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(ErrorResponseManager.buildValidationErrors(validateErrors)).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response userLoginAction(@PathParam("id") String userId, @Context HttpServletRequest requestContext) throws AppException {

        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        serviceResponse.put("user", user);

        return Response.status(HttpServletResponse.SC_OK).entity(serviceResponse).build();
    }
}
