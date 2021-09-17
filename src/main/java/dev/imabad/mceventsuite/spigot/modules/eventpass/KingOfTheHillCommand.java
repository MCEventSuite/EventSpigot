package dev.imabad.mceventsuite.spigot.modules.eventpass;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.commands.EventCommand;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.AwardPlayerXPMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class KingOfTheHillCommand extends BaseCommand {

    private static final Component KING_OF_THE_HILL = Component.text("KING OF THE HILL").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
    private static Component won(String username){
        return Component.text(username).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).append(Component.text(" was the ").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, false)).append(KING_OF_THE_HILL).append(Component.text("!").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, false));
    }
    private static Component tied(int count){
        return Component.text("It was a Tie! There were ").append(Component.text(count).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).append(Component.text(" players at the top of the hill.")).color(NamedTextColor.BLUE);
    }
    private static final Component NOBODY = Component.text("Nobody was ").append(KING_OF_THE_HILL).append(Component.text(", better luck next time!")).color(NamedTextColor.BLUE);

    //kothxp <time> <xp>

    public KingOfTheHillCommand() {
        super("kothxp", "eventsuite.kothxp");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!super.execute(sender, label, args)){
            return false;
        }
        if(args.length != 2){
            audience(sender).sendMessage(Component.text("Invalid usage: /kothxp <time> <xp>").color(NamedTextColor.RED));
            return false;
        }
        int time = Integer.parseInt(args[0]);
        int xp = Integer.parseInt(args[1]);
        Component announcement = Component.text("The player who is the ").append(KING_OF_THE_HILL).append(Component.text(" in the next ")).append(Component.text(time).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).append(Component.text(" seconds will earn ")).append(Component.text(xp).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).append(Component.text(" XP!")).color(NamedTextColor.BLUE);
        for(Player player : Bukkit.getOnlinePlayers()){
            audience(player).sendMessage(announcement);
        }
        Title.Times titleTimes = Title.Times.of(Ticks.duration(5), Ticks.duration(10), Ticks.duration(5));
        new BukkitRunnable() {
            int timer = time;
            @Override
            public void run() {
                timer--;
                if(timer < 5){
                    List<Player> playersInKOTH = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH")).collect(Collectors.toList());
                    playersInKOTH.forEach(player -> audience(player).showTitle(Title.title(Component.text(timer).color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD), Component.empty(), titleTimes)));
                }
                if(timer <= 0){
                    List<Player> playersOnTop = Bukkit.getOnlinePlayers().stream().filter(player -> RegionUtils.isInRegion(player, "KOTH-TOP")).collect(Collectors.toList());
                    Component message;
                    if(playersOnTop.size() == 1){
                        Player player = playersOnTop.get(0);
                        message = won(player.getName());
                        EventCore.getInstance().getModuleRegistry().getModule(
                                RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(player.getUniqueId(), xp, "Earned from King of the Hill!"));
                    } else if(playersOnTop.size() > 1){
                        int count = playersOnTop.size();
                        message = tied(count);
                        playersOnTop.forEach(player -> EventCore.getInstance().getModuleRegistry().getModule(
                                        RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(player.getUniqueId(), xp, "Earned from King of the Hill!")));
                    } else {
                        message = NOBODY;
                    }
                    for(Player p : Bukkit.getOnlinePlayers()){
                        audience(p).sendMessage(message);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(EventSpigot.getInstance(), 0L, 20L);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
