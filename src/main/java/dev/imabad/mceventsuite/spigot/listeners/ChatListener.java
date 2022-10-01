package dev.imabad.mceventsuite.spigot.listeners;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.util.BadWords;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.naming.Name;
import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final Component FILTERED_MESSAGE = Component.text("Your message was filtered").color(NamedTextColor.YELLOW);
    private static final Component SPAM_MESSAGE = Component.text("Please don't spam!").color(NamedTextColor.YELLOW);
    private static final Component MUTED_MESSAGE = Component.text("You have been muted!").color(NamedTextColor.RED)
            .append(Component.text("\nThink this was in error? Appeal to ").color(NamedTextColor.GRAY))
            .append(Component.text("support@cubedcon.com").color(NamedTextColor.BLUE));


    static class LastMessage {
        public String message;
        public long time;

        public LastMessage(String message, long time) {
            this.message = message;
            this.time = time;
        }
    }

    private HashMap<UUID, LastMessage> messages = new HashMap<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        messages.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        String messageAsString = LegacyComponentSerializer.legacyAmpersand().serialize(event.originalMessage());
        if (!messageAsString.startsWith("/")) {
            if (EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).getMutedPlayersManager().isMuted(sender.getUniqueId().toString())) {
                long expiry = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).getMutedPlayersManager().getMuteExpiry(sender.getUniqueId().toString());
                if (expiry > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(MUTED_MESSAGE);
                    return;
                } else {
                    EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).getMutedPlayersManager().removePlayer(sender.getUniqueId().toString());
                }
            }

            EventCore.getInstance().getEventPlayerManager().getPlayer(sender.getUniqueId()).ifPresent(player -> {
                // Check message for profanity.
                if (BadWords.badWordsFound(messageAsString).size() > 0) {
                    sender.sendMessage(FILTERED_MESSAGE);
                    event.setCancelled(true);
                    return;
                }
                if (messages.containsKey(sender.getUniqueId()) && !player.hasPermission("eventsuite.staffchat")) {
                    LastMessage lastMessage = messages.get(sender.getUniqueId());
                    long timeSince = System.currentTimeMillis() - lastMessage.time;
                    if (lastMessage.message.equalsIgnoreCase(messageAsString) && timeSince < 5000) {
                        sender.sendMessage(SPAM_MESSAGE);
                        event.setCancelled(true);
                        return;
                    } else if (timeSince < 2000) {
                        sender.sendMessage(SPAM_MESSAGE);
                        event.setCancelled(true);
                        return;
                    }
                }
                messages.put(player.getUUID(), new LastMessage(messageAsString, System.currentTimeMillis()));
            });
        }
    }
}