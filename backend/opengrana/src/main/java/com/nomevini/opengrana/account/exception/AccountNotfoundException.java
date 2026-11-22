package com.nomevini.opengrana.account.exception;

public class AccountNotfoundException extends RuntimeException {
    public AccountNotfoundException() {
        super("Account not found;");
    }
}
