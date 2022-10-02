package dev.imabad.mceventsuite.spigot.modules.shows;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.shows.actions.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.*;

public class Show {

    private World showWorld;
    private String name;
    private Set<ShowAction> actions;
    private Map<String, FireworkEffect> effectMap;
    private Location showLocation;
    private int radius = 75;

    public void displayText(String text){
        showLocation.getNearbyPlayers(radius).forEach((p) -> p.sendMessage(text));
    }

    private void loadShow(File showFile){
        this.actions = new HashSet<>();
        try(FileInputStream fs = new FileInputStream(showFile); DataInputStream ds = new DataInputStream(fs);
            BufferedReader br = new BufferedReader(new InputStreamReader(ds))){
            String readLine;
            while((readLine = br.readLine()) != null){
                if(readLine.length() != 0 && !readLine.startsWith("#")){
                    String[] segments = readLine.split("\\s+");
                    if(segments.length < 3){
                        //TODO: LOG INVALID LINE
                    }
                    else {
                        switch(segments[0].toUpperCase()){
                            case "SHOW":
                                switch(segments[1].toUpperCase()){
                                    case "LOCATION":
                                        String locationText = segments[2];
                                        String[] locationParts = locationText.split(",");
                                        if(locationParts.length < 3){
                                            //INVALID LOCATION
                                        } else {
                                            this.showLocation = new Location(showWorld, Double.parseDouble(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2]));
                                        }
                                        break;
                                    case "TEXTRADIUS":
                                        try {
                                            radius = Integer.parseInt(segments[2]);
                                        } catch (Exception e) {
                                            // INVALID TEXTRADIUS
                                        }
                                        break;
                                }
                                break;
                            case "EFFECT":
                                FireworkEffect effect = parseEffect(segments[2]);
                                if(effect == null){
                                    continue;
                                }
                                effectMap.put(segments[1], effect);
                                break;
                            default:
                                String[] timeTokens = segments[0].split("_");
                                long time = 0L;
                                for(int i = 0; i < timeTokens.length; i++){
                                    time = (long)(time + Double.parseDouble(timeTokens[i]) * 1000);
                                }
                                switch(segments[1].toUpperCase()){
                                    case "TEXT":
                                        StringBuilder textBuilder = new StringBuilder();
                                        for(int i = 2; i < segments.length; i++){
                                            textBuilder.append(segments[i]).append(" ");
                                        }
                                        String text = textBuilder.toString();
                                        if(text.length() > 1){
                                            text = text.substring(0, text.length() - 1);
                                        }
                                        this.actions.add(new TextAction(this, time, text));
                                        break;
                                    case "MUSIC":
                                        try {
                                            int id = Integer.parseInt(segments[2]);
                                            this.actions.add(new PlayMusicAction(this, time, id));
                                        } catch(Exception e){
                                            // INVALID MUSIC
                                        }
                                        break;
                                    case "PULSE":
                                        Location pulseLoc = locationFromString(showWorld.getName() + "," + segments[2]);
                                        if(pulseLoc == null){
                                            //Invalid Location
                                            break;
                                        }
                                        this.actions.add(new PulseAction(this, time, pulseLoc));
                                        break;
                                    case "LIGHTNING":
                                        Location lightningLoc = locationFromString(showWorld.getName() + "," + segments[2]);
                                        if(lightningLoc == null){
                                            //Invalid Location
                                            continue;
                                        }
                                        this.actions.add(new LightningAction(this, time, lightningLoc));
                                        break;
                                    case "NPC": break;
                                    case "BLOCK":
                                        Location blockLoc = locationFromString(showWorld.getName() + "," + segments[3]);
                                        if(blockLoc == null){
                                            //Invalid!
                                            break;
                                        }
                                        try {
                                            Material material = Material.getMaterial(segments[2].toUpperCase());
                                            this.actions.add(new BlockAction(this, time, blockLoc, material));
                                        } catch (Exception e){
                                            //Invalid!
                                        }
                                        break;
                                    case "FIREWRITE":
                                        if(segments.length < 6){
                                            //Invalid!
                                            break;
                                        }
                                        Location fireWriteLoc = locationFromString(showWorld.getName() + "," + segments[2]);
                                        if(fireWriteLoc == null){
                                            //Invalid location!
                                            break;
                                        }
                                        ArrayList<Object> effectList = new ArrayList<>();
                                        String[] effects = segments[3].split(",");
                                        for(String fireWriteEffect : effects){
                                            if(this.effectMap.containsKey(fireWriteEffect))
                                                effectList.add(this.effectMap.get(fireWriteEffect));
                                        }
                                        if(effectList.isEmpty()){
                                            //Invalid effects!
                                            break;
                                        }
                                        try {
                                            BlockFace dir = BlockFace.valueOf(segments[4]);
                                        }
                                        break;
                                    case "FIREWORK": break;

                                }
                                // Assume it's a time token
                                break;
                        }
                        if(segments[0].equalsIgnoreCase("Show")){
                            //TODO: Handle Show bits

                        } else if(segments[0].equalsIgnoreCase("Effect")){
                            // TODO
                        }
                    }
                }
            }
        } catch(FileNotFoundException fileNotFoundException){

        } catch(IOException ioException){

        }
    }


    private FireworkEffect parseEffect(String effectString){
        String[] segments = effectString.split(",");
        FireworkEffect.Type shape;
        try {
            shape = FireworkEffect.Type.valueOf(segments[0]);
        } catch (Exception e){
            EventSpigot.getInstance().getLogger().warning("Invalid firework effect: " + segments[0]);
            return null;
        }
        List<Color> colors = new ArrayList<>();
        String[] colorTexts = segments[1].split("&");
        for (String colorText : colorTexts) {
            Color color = colorFromString(colorText);
            if (color != null) {
                colors.add(color);
            } else {
                EventSpigot.getInstance().getLogger().warning("Invalid firework color: " + colorText);
            }
        }
        if(colors.isEmpty()){
            EventSpigot.getInstance().getLogger().warning("Invalid colors!");
            return null;
        }
        boolean flicker = effectString.toUpperCase().contains("FLICKER");
        boolean trail = effectString.toUpperCase().contains("TRAIL");
        return FireworkEffect.builder().with(shape).withColor(colors).withFade(colors.get(0)).flicker(flicker).trail(trail).build();
    }

    private Color colorFromString(String colorText) {
        return switch (colorText.toUpperCase()) {
            case "WHITE" -> Color.WHITE;
            case "SILVER" -> Color.SILVER;
            case "GRAY" -> Color.GRAY;
            case "BLACK" -> Color.BLACK;
            case "RED" -> Color.RED;
            case "MAROON" -> Color.MAROON;
            case "YELLOW" -> Color.YELLOW;
            case "OLIVE" -> Color.OLIVE;
            case "LIME" -> Color.LIME;
            case "GREEN" -> Color.GREEN;
            case "AQUA" -> Color.AQUA;
            case "TEAL" -> Color.TEAL;
            case "BLUE" -> Color.BLUE;
            case "NAVY" -> Color.NAVY;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "PURPLE" -> Color.PURPLE;
            case "ORANGE" -> Color.ORANGE;
            default -> null;
        };
    }

    private Location locationFromString(String locationString){
        String[] segments = locationString.split(",");
        try {
            for(World world : Bukkit.getWorlds()){
                if(world.getName().equalsIgnoreCase(segments[0])){
                    return new Location(world, Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]));
                }
            }
        } catch(Exception e){
            return null;
        }
        return null;
    }
}
