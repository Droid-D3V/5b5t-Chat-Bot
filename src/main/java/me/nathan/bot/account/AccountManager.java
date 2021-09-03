package me.nathan.bot.account;

import me.nathan.bot.client.Config;

public class AccountManager {

    public static Account account;

    public AccountManager() {
        account = new Account(Config.Email, Config.Password);
    }
}
