package com.fourstudents.jedzonko.Network.Responses;

public class RegisterResponse {
    private final int id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;
    private final String authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;

    public RegisterResponse(int id, String username,
                            String firstName,
                            String lastName,
                            boolean enabled,
                            String authorities,
                            boolean accountNonExpired,
                            boolean accountNonLocked,
                            boolean credentialsNonExpired) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
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

    public String getAuthorities() {
        return authorities;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
}
