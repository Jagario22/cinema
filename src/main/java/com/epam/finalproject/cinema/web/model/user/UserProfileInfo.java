package com.epam.finalproject.cinema.web.model.user;
import com.epam.finalproject.cinema.domain.entity.User.ROLE;
import com.epam.finalproject.cinema.domain.entity.Wallet;

import java.io.Serializable;

public class UserProfileInfo  implements Serializable {
    private int id = 1;
    private String login;
    private String email;
    private ROLE role;
    private Wallet wallet;

    public UserProfileInfo(int id, String login, String email, Wallet wallet, ROLE role) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.wallet = wallet;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "UserProfileInfo{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", wallet=" + wallet +
                '}';
    }
}
