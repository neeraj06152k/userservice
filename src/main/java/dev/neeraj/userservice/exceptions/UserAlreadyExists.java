package dev.neeraj.userservice.exceptions;

public class UserAlreadyExists extends Exception {
    public UserAlreadyExists(String msg) {
        super(msg);
    }
}
