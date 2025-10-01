package com.delivery.justonebite.exception.response;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private ErrorCode errorCode;
    private String description;
    private int status;

    public ErrorResponse(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
        this.status = errorCode.getStatus().value();
    }

    public ErrorResponse(ErrorCode errorCode, String description){
        this.errorCode = errorCode;
        this.description = description;
        this.status = errorCode.getStatus().value();
    }
}
