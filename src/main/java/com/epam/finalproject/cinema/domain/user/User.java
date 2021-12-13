package com.epam.finalproject.cinema.domain.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
/**
 * Describes user entity
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class User implements Serializable {
    private static final long serialVersionUID = 3484788342620963892L;
    private Integer id;
    private String email;
    private String password;
    private String login;
    private ROLE role;

    public enum ROLE {
        ADMIN,
        USER
    }

    public User() {
    }
    public User(String login) {
        this.login = login;

    }
    public User(String email, String password, String login, ROLE role) {
        this.email = email;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    public User(String email, String password, String login, ROLE role, BigDecimal balance) {
        this.email = email;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    public User(Integer id, String email, String password, String login, ROLE role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    public User(Integer id, String email, String login, ROLE role) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.role = role;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email) && password.equals(user.password) && login.equals(user.login) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, login);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", role=" + role +
                '}';
    }
}
