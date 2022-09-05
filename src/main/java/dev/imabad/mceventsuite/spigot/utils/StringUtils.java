package dev.imabad.mceventsuite.spigot.utils;

import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern RGB_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public static String formatRGB(String text){
        String legacyConverted = ChatColor.translateAlternateColorCodes('&', text);
        StringBuffer rgbString = new StringBuffer();
        Matcher matcher = RGB_PATTERN.matcher(legacyConverted);
        while(matcher.find()){
            try {
                String hexCode = matcher.group(1);
                matcher.appendReplacement(rgbString, parseRGB(hexCode));
            }catch(NumberFormatException ignored){}
        }
        matcher.appendTail(rgbString);
        return rgbString.toString();
    }

    public static String parseRGB(String input){
        if(input.startsWith("#")){
            input = input.substring(1);
        }
        if(input.length() != 6){
            throw new NumberFormatException("Invalid hex code");
        }
        StringBuilder parsedString = new StringBuilder("\u00a7x");
        for(char c : input.toCharArray()){
            parsedString.append("\u00a7").append(c);
        }
        return parsedString.toString();
    }

    public static final String FORMAT_CODE_START = "{";
    public static final String FORMAT_CODE_END = "}";

    public static String capitalizeFirstLetter(String original) {
        if (isStringNullorEmpty(original)) {
            return original;
        } else {
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }
    }

    public static String format(String msg, Object... replacements) {
        return colorizeMessage(replace(msg, replacements));
    }

    public static String colorizeMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean isStringNullorEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String replace(String msg, Object... replacements) {
        int count = 0;
        for (Object o : replacements) {
            String formatCode = formatCode(count);
            if (o != null && msg.contains(formatCode)) {
                msg = msg.replace(formatCode, o.toString());
            }
            count++;
        }
        return msg;
    }

    private static String formatCode(int number) {
        return FORMAT_CODE_START + number + FORMAT_CODE_END;
    }

    public static String trim(String string, int size) {
        return string.length() > size ? string.substring(0, size - 1) : string;
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}
