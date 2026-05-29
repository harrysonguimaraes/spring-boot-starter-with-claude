package com.example.helloworld.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String error, List<String> details) {

    public ErrorResponse(String error) {
        this(error, null);
    }
}
