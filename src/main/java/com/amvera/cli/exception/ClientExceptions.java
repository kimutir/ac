package com.amvera.cli.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class ClientExceptions {

    public static HttpClientErrorException noContent(String message) {
        return new HttpClientErrorException(message, HttpStatus.NO_CONTENT, null, null, null, null);
    }

}
