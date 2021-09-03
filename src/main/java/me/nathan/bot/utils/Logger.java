package me.nathan.bot.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
    private static String time = dateFormat.format(new Date());

    public static void info(String message) {
        System.out.println("[" + time + "] " + message);
    }
}
