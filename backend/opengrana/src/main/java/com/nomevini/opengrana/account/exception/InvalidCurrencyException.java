package com.nomevini.opengrana.account.exception;

public class InvalidCurrencyException extends RuntimeException {
    public InvalidCurrencyException(String code) {
        super("Unsupported currency code:" + code);
    }
}
