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
        System.out.println(" -- NEW BUBBLE MANAGER --");
        Thread.dumpStack();
        this.chatBubbleMap = new ConcurrentHashMap<>();
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
            System.out.println(bubble.getName() + " " + bubble.getMembers().size());
            if(bubble.getMembers().size() > 0)
                System.out.println(bubble.getMembers().get(0).toString() + " vs " + uuid.toString());
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
        final ChatBubble bubble = this.chatBubbleMap.get(bubbleName);
        if(bubble == null) {
            player.sendMessage(ChatColor.RED + "Chat bubble " + bubbleName + " does not exist!");
            return;
        }

        bubble.getMembers().add(player.getUniqueId());
        System.out.println(bubble.getMembers().size());
        System.out.println(chatBubbleMap.get(bubble.getName()).getMembers().size());
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

        System.out.println("leaving bubble");

        bubble.getMembers().remove(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have left the chat bubble for " + ChatColor.AQUA +
                bubble.getName());
    }
}
