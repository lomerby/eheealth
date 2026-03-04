package com.ehealthcare.utils;

import com.ehealthcare.models.User;

/**
 * AuthResult is a simple response wrapper returned by the AuthService.
 * Carries success/failure state, the authenticated user, and a message.
 */
public class AuthResult {

    private final boolean success;
    private final User user;
    private final String message;

    private AuthResult(boolean success, User user, String message) {
        this.success = success;
        this.user = user;
        this.message = message;
    }

    public static AuthResult success(User user, String message) {
        return new AuthResult(true, user, message);
    }

    public static AuthResult failure(String message) {
        return new AuthResult(false, null, message);
    }

    public boolean isSuccess() { return success; }
    public User getUser() { return user; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "AuthResult{success=" + success + ", message='" + message + "'}";
    }
}
