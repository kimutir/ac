package com.amvera.cli.exception;

import java.io.IOException;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
