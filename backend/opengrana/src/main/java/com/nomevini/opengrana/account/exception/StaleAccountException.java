package com.nomevini.opengrana.account.exception;

/** If-Match não corresponde à versão atual. */
public class StaleAccountException extends RuntimeException {
    public StaleAccountException(long expected, long actual)
    {
        super("Stale account version: expected " + expected + "but was" + actual);
    }
}
