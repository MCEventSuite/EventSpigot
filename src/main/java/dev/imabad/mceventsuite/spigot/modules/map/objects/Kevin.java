package dev.imabad.mceventsuite.spigot.modules.map.objects;

import net.citizensnpcs.npc.skin.Skin;

import java.util.List;

public class Kevin {

    private String name;
    private SkinData skin;
    private String model;
    private Double posX;
    private Double posY;
    private Double posZ;
    private float facing;
    private boolean trackPlayer;
    private List<String> voiceLines;

    public Kevin(){}

    public String getName() {
        return name;
    }

    public SkinData getSkin() {
        return skin;
    }

    public String getModel() {
        return model;
    }

    public Double getPosX() {
        return posX;
    }

    public Double getPosY() {
        return posY;
    }

    public Double getPosZ() {
        return posZ;
    }

    public float getFacing() {
        return facing;
    }

    public boolean isTrackPlayer() {
        return trackPlayer;
    }

    public List<String> getVoiceLines() {
        return voiceLines;
    }

    public static class SkinData {
        private String value;
        private String signature;

        public SkinData() {}

        public String getValue() {
            return value;
        }

        public String getSignature() {
            return signature;
        }
    }
}
