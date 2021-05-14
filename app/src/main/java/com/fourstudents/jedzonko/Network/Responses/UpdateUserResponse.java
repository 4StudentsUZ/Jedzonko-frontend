package com.fourstudents.jedzonko.Network.Responses;

public class UpdateUserResponse {
    private final int id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;

    public UpdateUserResponse(int id, String username,
                            String firstName,
                            String lastName,
                            boolean enabled) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
