package dev.imabad.mceventsuite.spigot.modules.bubbles;

import com.sk89q.worldedit.WorldEdit;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.bubbles.redis.GetBubblesRequest;
import dev.imabad.mceventsuite.spigot.modules.bubbles.redis.GetBubblesResponse;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BubbleManager {
    private final Map<String, ChatBubble> chatBubbleMap;

    public BubbleManager() {
        this.chatBubbleMap = new HashMap<>();
        this.chatBubbleMap.put("Main Stage", new ChatBubble("Main Stage", "Stage", "mainstage"));
        this.chatBubbleMap.put("Community Stage", new ChatBubble("Community Stage", "Stage", "communitystage"));
        EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class)
                .publishRequest(RedisChannel.GLOBAL, new GetBubblesRequest(), (response) -> {
                    if (response instanceof GetBubblesResponse bubblesResponse) {
                        for (ChatBubble bubble : bubblesResponse.getBubbleList()) {
                            chatBubbleMap.putIfAbsent(bubble.getName(), bubble);
                        }
                    }
                });
    }

    public Collection<ChatBubble> getChatBubbles() {
        return this.chatBubbleMap.values();
    }

    public ChatBubble getChatBubble(UUID uuid) {
        for(ChatBubble bubble : this.chatBubbleMap.values()) {
            if (bubble.getMembers().contains(uuid)) {
                return bubble;
            }
        }
        return null;
    }

    public void joinMeetBubble(Player player, String meet, int position) {
        for(ChatBubble bubble : this.chatBubbleMap.values()) {
            if(bubble.getDisplayName().equalsIgnoreCase("Meet")) {
                bubble.getMembers().remove(player.getUniqueId());
            }
        }

        ChatBubble chatBubble = this.chatBubbleMap.get(meet + "-" + position);
        if(chatBubble == null) {
            chatBubble = new ChatBubble(meet + "-" + position, "Meet", null);
            chatBubble.getMembers().add(player.getUniqueId());
            this.chatBubbleMap.put(meet + "-" + position, chatBubble);
        }

        player.sendMessage(ChatColor.GREEN + "You have entered the chat bubble for " + ChatColor.AQUA + meet + " position " + position);
    }

    public void joinChatBubble(Player player, String bubbleName) {
        ChatBubble bubble = this.chatBubbleMap.get(bubbleName);
        if(bubble == null && !bubbleName.contains("-")) {
            player.sendMessage(ChatColor.RED + "Chat bubble " + bubbleName + " does not exist!");
            return;
        } else if(bubble == null) {
            bubble = new ChatBubble(bubbleName, "Meet", null);
            this.chatBubbleMap.put(bubbleName, bubble);
        }

        player.sendMessage(ChatColor.GREEN + "You have entered the chat bubble for " + ChatColor.AQUA +
                bubble.getName());
    }

    public void leaveAllChatBubbles(Player player, boolean meetOnly) {
        for(ChatBubble bubble : this.chatBubbleMap.values()) {
            if(meetOnly && !bubble.getName().contains("-"))
                continue;
            bubble.getMembers().remove(player.getUniqueId());
        }
    }

    public void leaveChatBubble(Player player, String bubbleName) {
        final ChatBubble bubble = this.chatBubbleMap.get(bubbleName);
        if(bubble == null)
            return;

        bubble.getMembers().remove(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have left the chat bubble for " + ChatColor.AQUA +
                bubble.getName());
    }
}
