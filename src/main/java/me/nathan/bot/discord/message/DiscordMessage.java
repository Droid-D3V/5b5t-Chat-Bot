package me.nathan.bot.discord.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordMessage {

    private String message;
    private String author;
    private String[] sections;

    public DiscordMessage(String message, String author) {
        this.message = message;
        this.author = author;

        if(message != null) {
            sections = message.split(" ");
        }
    }

    private static final Pattern joindatePattern = Pattern.compile("/joindate ([A-Za-z0-9_]+)");
    private static final Pattern allowedChars = Pattern.compile("[A-Za-z0-9_*./#$,;@^&+=|><')(%!?-]+");

    public boolean isCommand() {
        if(message.startsWith("/")) return true;
        return false;
    }

    public boolean isAllowedCommand() {
       if(isCommand()) {
           if(sections[0].equalsIgnoreCase("/joindate")) {
               return true;
           }
           if(sections[0].equalsIgnoreCase("/msg")) {
               return true;
           }
           if(sections[0].equalsIgnoreCase("/w")) {
               return true;
           }
           if(sections[0].equalsIgnoreCase("/r")) {
               return true;
           }
       }
       return false;
    }

    public String getMessage() {
        if(message.length() + author.length() + 2 > 65) return "[Node3112] NathanW_ is the king of constantiam";
        for(String s : sections) {
            Matcher matcher = allowedChars.matcher(s);
            if(!matcher.matches()) return "[ianmc05/mvc4209] NathanW_ owns me";
        }
        if(isAllowedCommand()) {
            if(sections[0].equalsIgnoreCase("/joindate")) {
                Matcher matcher = joindatePattern.matcher(message);
                if(!matcher.matches()) return "[arzi] NathanW_ is a pro hacker";
            }
            return message;
        } else if(!isCommand()) {
            return "[" + author + "] " + message;
        }
         return null;
    }
}
