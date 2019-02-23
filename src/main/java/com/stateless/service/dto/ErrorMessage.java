package com.stateless.service.dto;

import java.util.HashMap;
import java.util.List;

public class ErrorMessage {
    private List<HashMap> errors;

    public ErrorMessage() {
    }

    public ErrorMessage(List<HashMap> errors) {
        this.errors = errors;
    }

    public List<HashMap> getErrors() {
        return errors;
    }

    public void setErrors(List<HashMap> errors) {
        this.errors = errors;
    }
}
