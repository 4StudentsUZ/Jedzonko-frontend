package com.fourstudents.jedzonko.Network.Responses;

public class LoginResponse {
    private final String username;
    private final String token;

    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

}
