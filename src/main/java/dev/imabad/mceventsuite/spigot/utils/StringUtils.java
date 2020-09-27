package dev.imabad.mceventsuite.spigot.utils;

import org.bukkit.ChatColor;
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
}
