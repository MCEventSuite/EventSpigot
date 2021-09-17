package dev.imabad.mceventsuite.spigot.modules.daylight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class is a modified version of the class at: https://bukkit.org/threads/resource-iputils-get-players-weather-city-local-time-and-more.263530/
 */
public class IPUtils {

    //Stores the IP address and a bunch of info about them
    static HashMap<String,JSONObject> ipStorage = new HashMap<String,JSONObject>();

    public IPUtils() {
    }

    public JSONObject getFromRedis(String ip) {
        return EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).getFromHash("ipData", ip, JSONObject.class);
    }

    public void storeInRedis(String ip, JSONObject data) {
        EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).addToHash("ipData", ip, data);
    }

    public static String ipToTime(String ip) throws MalformedURLException {
        //offset from UTC
        int offset = 0;

        //If we already have their timezones, just grab if
        if (ipStorage.containsKey(ip)) {
            String timezone = ipStorage.get(ip).get("timeZone").toString();
            offset = Integer.parseInt(timezone.substring(0,timezone.length()-3));

        }
        //If not, get it
        else {
            String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip=" +
                    ip +
                    "&format=json";

            JSONObject object = stringToJSON(getUrlSource(url));
            String timezone = (String) object.get("timeZone");
            if (timezone != null && timezone.length() > 3) {
                offset = Integer.parseInt(timezone.substring(0,timezone.length()-3));
                ipStorage.put(ip,object);
            } else {
                return "Error: Cannot parse time";
            }
        }
        long time = System.currentTimeMillis();
        long offsetHours = offset * (1000 * 60 * 60);

        return String.valueOf(time + offsetHours);
    }

    public static JSONObject stringToJSON(String json){
        return (JSONObject) JSONValue.parse(json);
    }
    private static String getUrlSource(String url) throws MalformedURLException {
        URL url2 = new URL(url);
        URLConnection yc = null;
        try {
            yc = url2.openConnection();
        } catch (IOException e) {
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(), "UTF-8"));
        } catch (IOException e) {
        }
        String inputLine;
        StringBuilder a = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
        } catch (IOException e) {
        }
        try {
            in.close();
        } catch (IOException e) {
        }

        return a.toString();
    }
}
