package dev.imabad.mceventsuite.spigot.modules.bubbles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatBubble {
    private String name;
    private String displayName;

    private List<UUID> members;
    private String mappedRegion;

    public ChatBubble(String name, String displayName, String mappedRegion) {
        this.name = name;
        this.displayName = displayName;
        this.mappedRegion = mappedRegion;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<UUID> getMembers() {
        return this.members;
    }

    public String getMappedRegion() {
        return this.mappedRegion;
    }
}
