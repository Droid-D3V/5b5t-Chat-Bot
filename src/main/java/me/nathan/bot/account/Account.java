package me.nathan.bot.account;

import com.github.steveice10.packetlib.Client;

public class Account {

    public String email;
    public String password;
    public Client client;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
