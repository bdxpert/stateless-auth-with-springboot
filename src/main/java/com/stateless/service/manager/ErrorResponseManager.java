package com.stateless.service.manager;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.*;
//http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/

public class ErrorResponseManager {

    public static LinkedHashMap<Object, Object> buildValidationErrors(
            Set<ConstraintViolation<Object>> validateErrors) {
        List<Map<Object, Object>> errorsList = new ArrayList<>();
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        LinkedHashMap<Object, Object> apiResponse = new LinkedHashMap<Object, Object>();

        for (ConstraintViolation<Object> error : validateErrors) {
            LinkedHashMap<Object, Object> errorMap = new LinkedHashMap<Object, Object>();
            errorMap.put("field", error.getPropertyPath().toString());
            errorMap.put("message", error.getMessage());

            errorsList.add(errorMap);
        }

        response.put("name", "ValidationError");
        response.put("statusCode", Response.Status.BAD_REQUEST.getStatusCode());
        response.put("details", errorsList);

        apiResponse.put("error", response);

        return apiResponse;
    }
}
