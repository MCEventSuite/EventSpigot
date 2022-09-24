package dev.imabad.mceventsuite.spigot.modules.meet;

import com.github.puregero.multilib.MultiLib;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.modules.redis.RedisMessageListener;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.PlayerMoveQueueMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.PlayerTimeReminderMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.ServerAnnounceDisplayNames;
import dev.imabad.mceventsuite.core.modules.redis.messages.meet.ServerAnnounceMeetState;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.bossbar.BossBarModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.inject.Named;
import javax.naming.Name;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MeetModule extends Module {

    private HashMap<String, String> displayNames;

    @Override
    public String getName() {
        return "meet";
    }

    @Override
    public void onEnable() {
        final RedisModule redisModule = EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class);
        EventSpigot.getInstance().getCommandMap().register("meetgreet", new MeetCommand(this));
        EventSpigot.getInstance().getCommandMap().register("meet", new SimplifiedMeetCommand(this));

        redisModule.registerListener(ServerAnnounceDisplayNames.class,
                new RedisMessageListener<>((msg) -> this.displayNames = msg.getDisplayNames()));

        redisModule.registerListener(PlayerMoveQueueMessage.class, new RedisMessageListener<>((msg) -> {
            final UUID uuid = msg.getUuid();
            final Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isLocalPlayer())
                return;

            if(msg.isQueue()) {
                player.sendMessage(ChatColor.GRAY + "You are position " + msg.getPosition() + " in the queue for the " +
                        "Meet & Greet with " + this.getDisplayName(msg.getName()) + ".\nEstimated " + msg.getEta() + " minute wait.");
            } else if(msg.getPosition() == 0) {
                player.sendMessage(ChatColor.GREEN + "You're up for the Meet & Greet with " +
                        ChatColor.AQUA + this.getDisplayName(msg.getName()) + ChatColor.GREEN + "!\n" +
                        ChatColor.RESET + "Your session will last for " + ChatColor.YELLOW + msg.getEta() + " minutes");
            } else if(msg.getPosition() != -1) {
                player.sendMessage(ChatColor.GREEN + "You're up for the next spot on the Meet & Greet with " +
                        ChatColor.AQUA + this.getDisplayName(msg.getName()) + ChatColor.GREEN + "!\n" +
                        ChatColor.RESET + "Your session will last for " + ChatColor.YELLOW + msg.getEta() + " minutes");
            } else {
                player.teleport(new Location(
                        Bukkit.getWorld("world"), 0.5, 30, 8.5, 0, 0));
                player.sendMessage(ChatColor.RED + "Your Meet & Greet session with " + ChatColor.AQUA + this.getDisplayName(msg.getName()) +
                        ChatColor.RESET + " has ended.\n" + ChatColor.RESET + "Thank you for stopping by. See you soon!");
            }

            if(msg.getLocation() != null) {
                EventSpigot.getInstance().getServer().getScheduler().runTask(EventSpigot.getInstance(),
                        () -> player.teleport(new Location(
                                Bukkit.getWorld("world"), msg.getLocation().getX(), msg.getLocation().getY(), msg.getLocation().getZ())));

            }
        }));

        redisModule.registerListener(PlayerTimeReminderMessage.class, new RedisMessageListener<>((msg) -> {
            final Player player = Bukkit.getPlayer(msg.getUuid());
            if(player == null || player.isExternalPlayer())
                return;

            player.sendMessage(ChatColor.GRAY + "You have " + ChatColor.YELLOW + msg.getTimeString() + ChatColor.GRAY + " left of your Meet & Greet session.");
        }));

        redisModule.registerListener(ServerAnnounceMeetState.class, new RedisMessageListener<>((msg) -> {
            if(msg.isStarted()) {
                EventCore.getInstance().getModuleRegistry().getModule(BossBarModule.class)
                        .addMeetGreet(msg.getName(), this.getDisplayName(msg.getName()), msg.getEnds());
                Component component = Component.text("----------------------------").color(NamedTextColor.BLUE)
                        .append(Component.text("\n\nMeet & Greet\n").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text("A Meet & Greet session is starting with ").color(NamedTextColor.GREEN))
                        .append(Component.text(this.getDisplayName(msg.getName())).color(NamedTextColor.AQUA))
                        .append(Component.text("!").color(NamedTextColor.GREEN))
                        .append(Component.text("\n\nClick here").color(NamedTextColor.YELLOW)
                                .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                .clickEvent(ClickEvent.runCommand("/meet " + msg.getName()))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to join!").color(NamedTextColor.GREEN))))
                        .append(Component.text(" to join the the queue, or use ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.UNDERLINED, false))
                        .append(Component.text("/meet " + msg.getName()).color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text("\n\n----------------------------").color(NamedTextColor.BLUE));

                for(Player player : Bukkit.getLocalOnlinePlayers())
                    player.sendMessage(component);
            } else {
                for(Player player : Bukkit.getLocalOnlinePlayers())
                    player.sendMessage(ChatColor.RED + "The Meet & Greet session with " + ChatColor.AQUA + this.getDisplayName(msg.getName()) +
                            ChatColor.RED + " has ended.\n" + ChatColor.RESET + "Thank you for stopping by. See you soon!");
            }
        }));

    }

    @Override
    public void onDisable() {

    }

    public String getDisplayName(String name) {
        if(this.displayNames == null)
            return name;
        if(!this.displayNames.containsKey(name))
            return name;
        return this.displayNames.get(name);
    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return null;
    }
}
