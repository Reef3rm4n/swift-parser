package com.six.swiftparser.domain.exception;

public class InvalidMessage extends Exception {
    public InvalidMessage(Exception e) {
        super(e);
    }

    public InvalidMessage(String e) {
        super(e);
    }
}
