package com.example.social_network_gui.domain;

import java.util.Objects;

public class RoleType extends Entity<Long>{
    private User user;
    private String role;

    public RoleType(User user, String role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleType roleType = (RoleType) o;
        return Objects.equals(user, roleType.user) && Objects.equals(role, roleType.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }

    @Override
    public String toString() {
        return "RoleType{" +
                "user=" + user +
                ", role='" + role + '\'' +
                '}';
    }
}
