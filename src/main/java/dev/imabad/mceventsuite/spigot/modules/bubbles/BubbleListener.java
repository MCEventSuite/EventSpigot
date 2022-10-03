package dev.imabad.mceventsuite.spigot.modules.bubbles;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class BubbleListener implements Listener {

    private BubbleManager bubbleManager;

    public BubbleListener(BubbleManager bubbleManager) {
        this.bubbleManager = bubbleManager;
    }

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        for(ChatBubble chatBubble : bubbleManager.getChatBubbles()) {
            if(chatBubble.getMappedRegion() != null && chatBubble.getMappedRegion().equalsIgnoreCase(event.getRegionId())) {
                bubbleManager.joinChatBubble(event.getPlayer(), chatBubble.getName());
            }
        }
    }

    @EventHandler
    public void onRegionLeft(RegionLeftEvent event) {
        for(ChatBubble chatBubble : bubbleManager.getChatBubbles()) {
            if(chatBubble.getMappedRegion() != null && chatBubble.getMappedRegion().equalsIgnoreCase(event.getRegionId()))
                bubbleManager.leaveChatBubble(event.getPlayer(), chatBubble.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for(ChatBubble bubble : this.bubbleManager.getChatBubbles())
            bubble.getMembers().remove(event.getPlayer().getUniqueId());
    }
}
