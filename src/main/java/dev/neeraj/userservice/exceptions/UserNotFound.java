package dev.neeraj.userservice.exceptions;

public class UserNotFound extends Exception {
    public UserNotFound(String msg) {
        super(msg);
    }
}
