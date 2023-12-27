package com.amvera.cli.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

public class ClientResponseException extends RestClientResponseException {
    public ClientResponseException(String msg, HttpStatus status) {
        super(
                String.format("%d. %s", status.value(), msg),
                status.value(),
                status.toString(),
                null, null, null
        );
    }
}
