package me.nathan.bot;

import me.nathan.bot.account.AccountManager;
import me.nathan.bot.client.Connection;
import me.nathan.bot.discord.ChatBridge;
import me.nathan.bot.utils.FileHelper;
import me.zero.alpine.bus.EventManager;

import java.io.IOException;

public class Main {
    public static EventManager EventManager = new EventManager();

    public static AccountManager accountManager;
    public static ChatBridge bot;

    public static void main(String[] args) throws IOException {
        FileHelper.readConfig();
        accountManager = new AccountManager();
        bot = new ChatBridge();

        Connection.connect(AccountManager.account);
    }
}
