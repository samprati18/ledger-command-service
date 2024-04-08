package com.assignment.ledger.exception;

public class PostingsNotFoundException extends RuntimeException {

    public PostingsNotFoundException(String message) {
        super(message);
    }
}
