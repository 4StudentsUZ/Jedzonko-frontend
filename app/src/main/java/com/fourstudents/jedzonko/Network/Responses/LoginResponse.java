package com.fourstudents.jedzonko.Network.Responses;

public class LoginResponse {
    private final int id;
    private final String username;
    private final String token;

    public LoginResponse(int id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public int getId() {return id;}

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

}
