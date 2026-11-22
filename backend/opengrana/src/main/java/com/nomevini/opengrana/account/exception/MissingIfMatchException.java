package com.nomevini.opengrana.account.exception;

public class MissingIfMatchException extends RuntimeException {
    public MissingIfMatchException()
    {
        super("If-Match header is required");
    }
}
