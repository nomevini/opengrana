package com.nomevini.opengrana.account.exception;

public class InvalidIfMatchException extends RuntimeException {
    public InvalidIfMatchException() {
        super("If-Match header is invalid");
    }
}
