package me.nathan.bot.discord.message;

import me.nathan.bot.discord.MessageHelper;
import me.nathan.bot.utils.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessage {

    private final String unformatted;
    private final String formatted;

    private static final Pattern usernamePattern = Pattern.compile("<[A-Za-z0-9_]+>");
    private static final Pattern toPattern = Pattern.compile("To ([A-Za-z0-9_]+)");
    private static final Pattern whispersPattern = Pattern.compile("[A-Za-z0-9_]+ whispers");
    private static final Pattern joinedPattern = Pattern.compile("([A-Za-z0-9_]+ joined the game)");
    private static final Pattern leftPattern = Pattern.compile("([A-Za-z0-9_]+ left the game)");

    String[] sections;

    public ChatMessage(String formatted, String unformatted) {
        this.unformatted = unformatted;
        this.formatted = formatted;

        sections = unformatted.split(" ");
    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    public String getSenderName() {
        if(isWhisperMessage()) {
            return sections[0];
        }
        if(isToMessage()) {
            return removeLastChar(sections[1]);
        }
        if(isNormalMessage()) {
            String s = sections[0].substring(1, sections[0].length() - 1);
            return s;
        }
        return null;
    }

    public boolean isNormalMessage() {
        Matcher matcher = usernamePattern.matcher(sections[0]);
        return matcher.matches();
    }

    public boolean isWhisperMessage() {
        String[] s = unformatted.split(":");
        Matcher matcher = whispersPattern.matcher(s[0]);
        return matcher.matches();
    }

    public boolean isToMessage() {
        String[] s = unformatted.split(":");
        Matcher matcher = toPattern.matcher(s[0]);
        return matcher.matches();
    }

    public boolean isGreenTextMessage() {
        if(formatted.contains("green")) return true;
        return false;
    }

    public boolean isServerMessage() {
        if(formatted.contains("gold") && !formatted.contains("dark_aqua")) return true;
        return false;
    }

    public boolean isDeathMessage() {
        if((formatted.contains("dark_aqua") && formatted.contains("gray")) || (formatted.contains("dark_aqua") && formatted.contains("gold"))) return true;
        return false;
    }

    public boolean isLogMessage() {
        Matcher m = leftPattern.matcher("Nathan left the game");

        Matcher matcherJoin = joinedPattern.matcher(unformatted);
        Matcher matcherLeave = leftPattern.matcher(unformatted);
        return matcherJoin.matches() || matcherLeave.matches();
    }

    public String getMessage() {
        String[] arrowSeparatorGreen = unformatted.split(">");
        String[] arrowSeparatorNormal = unformatted.split("> ", 2);
        String[] whisperSeparator = unformatted.split(": ");

        if(isToMessage()) return "-> " + whisperSeparator[1];
        if(isGreenTextMessage()) return arrowSeparatorGreen[2];

        if(isWhisperMessage()) return whisperSeparator[1];
        if(isNormalMessage())  {
            Logger.info("ww");
            Logger.info(arrowSeparatorNormal[0]);
            return arrowSeparatorNormal[1];
        }
        return unformatted;
    }

    public MessageHelper.MessageType getType() {
        if (isLogMessage()) return MessageHelper.MessageType.LOG;
        if (isWhisperMessage()) return MessageHelper.MessageType.WHISPER;
        if (isToMessage()) return MessageHelper.MessageType.WHISPER;
        if (isGreenTextMessage()) return MessageHelper.MessageType.GREENTEXT;
        if (isServerMessage()) return MessageHelper.MessageType.SERVER;
        if (isDeathMessage()) return MessageHelper.MessageType.DEATH;

        return MessageHelper.MessageType.NORMAL;
    }

}
