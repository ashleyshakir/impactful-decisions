package com.example.impactfuldecisions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DecisionExistsException extends RuntimeException{
    public DecisionExistsException(String message) {super(message);}
}
