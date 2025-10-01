package com.delivery.justonebite.exception.custom;

import com.delivery.justonebite.exception.response.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String description;
    private final HttpStatus status;


    public CustomException(ErrorCode errorCode){
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
        this.status = errorCode.getStatus();
    }

    public CustomException(ErrorCode errorCode, String description){
        super(description);
        this.errorCode = errorCode;
        this.description = description;
        this.status = errorCode.getStatus();
    }

}
