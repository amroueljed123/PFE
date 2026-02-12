package com.esb.appback.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private UserDto user;
    private String token;

    public LoginResponse(boolean success, String message, UserDto user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public LoginResponse(boolean success, String message, UserDto user, String token) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

