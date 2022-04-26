package com.unfamilia.application.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {
    private String error;
    private Integer errorCode;
}
