package com.stateless.service.controllers;

import com.stateless.service.entities.User;
import com.stateless.service.exceptions.AppException;
import com.stateless.service.services.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sanjiv on 9/9/20.
 */
@Controller
@Path("/search")
public class SearchController {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SearchService searchService;
    /******
     * Search content using mongodb4.2 search
     *
     * @return
     * @throws AppException
     */
    @GET
    @Path("/content")
    @Produces("application/json")
    public Response userLoginAction(@QueryParam("query") String queryStr) throws AppException {

        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        try {
            serviceResponse.put("data", searchService.find(queryStr));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return Response.status(HttpServletResponse.SC_OK).entity(serviceResponse).build();
    }
}
