package dev.neeraj.userservice.exceptions;

public class InvalidSignupRequest extends Exception {
    public InvalidSignupRequest(String msg) {
        super(msg);
    }
}
