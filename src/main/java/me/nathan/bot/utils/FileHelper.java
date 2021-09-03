package me.nathan.bot.utils;

import me.nathan.bot.client.Config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {

    public static void readConfig() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("Config.txt")));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] sections = line.split(" ");

            switch(sections[0]) {
                case "email:": Config.Email = sections[1]; break;
                case "password:": Config.Password = sections[1]; break;
                case "host:": Config.Host = sections[1]; break;
                case "reconnectdelay:": Config.ReconnectTime = Integer.parseInt(sections[1]); break;
                case "token:" : Config.Token = sections[1]; break;
                case "channel:" : Config.Channel = sections[1]; break;
                case "cooldown:" : Config.Cooldown = Integer.parseInt(sections[1]); break;

                default: Logger.info("Failed to read config");
            }
        }
        reader.close();
    }
}
