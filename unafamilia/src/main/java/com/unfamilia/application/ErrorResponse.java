package com.unfamilia.application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {
    private String error;
    private Integer errorCode;
}
