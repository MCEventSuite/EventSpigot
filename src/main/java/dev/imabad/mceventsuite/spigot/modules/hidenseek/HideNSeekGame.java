package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import com.github.puregero.multilib.MultiLib;
import com.sk89q.worldguard.WorldGuard;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HideNSeekGame {

    public enum GameStatus {
        WAITING,
        STARTED,
        ENDED;
    }

    private GameStatus status;
    private List<UUID> seekers;
    private List<UUID> hiders;

    private BukkitTask countdown;
    int counter = 600;

    public HideNSeekGame(){
        this.status = GameStatus.WAITING;
        this.seekers = new ArrayList<>();
        this.hiders = new ArrayList<>();
    }

    public UUID getStarter() {
        return this.seekers.get(0);
    }

    public List<UUID> getSeekers() {
        return this.seekers;
    }

    public List<UUID> getHiders() {
        return this.hiders;
    }

    public List<UUID> getAllPlayers() {
        List<UUID> players = new ArrayList<>(getSeekers());
        players.addAll(getHiders());
        return players;
    }

    public void addSeeker(UUID uuid) {
        final Player seeker = Bukkit.getPlayer(uuid);
        if (seeker == null)
            return;

        this.seekers.add(uuid);

        if (seeker.isLocalPlayer()) {
            seeker.sendMessage(ChatColor.GREEN + "YOU ARE NOW A SEEKER!");

            if (getStatus() == GameStatus.WAITING) {
                seeker.sendMessage(ChatColor.YELLOW + "You are hidden from the seekers until the game begins!");
            } else {
                seeker.sendMessage(ChatColor.YELLOW + "Punch a hider when you find them to catch them out!");
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (seekers.contains(player.getUniqueId()))
                    seeker.showPlayer(EventSpigot.getInstance(), player);
                else if (getStatus() == GameStatus.STARTED && hiders.contains(player.getUniqueId()))
                    seeker.showPlayer(EventSpigot.getInstance(), player);
                else
                    seeker.hidePlayer(EventSpigot.getInstance(), player);
            }
        }
    }

    public void leaveSeeker(UUID uuid) {
        this.seekers.remove(uuid);

        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        if(player.isLocalPlayer()) {
            for(Player p : Bukkit.getAllOnlinePlayers())
                player.showPlayer(EventSpigot.getInstance(), p);
        }

        for(Player player1 : Bukkit.getLocalOnlinePlayers()) {
            if(seekers.contains(player1.getUniqueId()) || hiders.contains(player1.getUniqueId())) {
                player1.hidePlayer(EventSpigot.getInstance(), player);
            }
        }
    }

    public void convertToSeeker(UUID uuid, String nameOfFinder) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        if(this.hiders.contains(uuid)) {
            this.hiders.remove(uuid);
            if(player.isLocalPlayer()) {
                player.sendMessage(ChatColor.RED + nameOfFinder.toUpperCase() + "HAS CAUGHT YOU!");
            }
            addSeeker(uuid);
        }
    }

    public void join(UUID uuid) {
        Player hider = Bukkit.getPlayer(uuid);
        if (hider == null)
            return;

        if(this.seekers.contains(uuid))
            return;

        if (status == GameStatus.WAITING) {
            this.hiders.add(uuid);

            if(hider.isLocalPlayer()) {
                hider.sendMessage(ChatColor.GREEN + "YOU ARE NOW A HIDER!");
                hider.sendMessage(ChatColor.YELLOW + "You are hidden from the seekers until the game begins!");
            }

            for(Player player : Bukkit.getAllOnlinePlayers()) {
                if(!hiders.contains(player.getUniqueId()) && !seekers.contains(player.getUniqueId()))
                    hider.hidePlayer(EventSpigot.getInstance(), player);
            }

            for (UUID seeker : seekers) {
                final Player player = Bukkit.getPlayer(seeker);
                if (player == null) {
                    seekers.remove(seeker);
                    continue;
                }

                if (player.isLocalPlayer())
                    player.hidePlayer(EventSpigot.getInstance(), hider);
            }
        }
    }

    public void leave(UUID uuid){
        this.hiders.remove(uuid);

        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        for(Player player1 : Bukkit.getLocalOnlinePlayers()) {
            if(hiders.contains(player1.getUniqueId()) || seekers.contains(player1.getUniqueId()))
                player1.hidePlayer(EventSpigot.getInstance(), player);
        }
    }

    public void start(boolean remote) {
        if(this.status != GameStatus.WAITING)
            return;
        this.status = GameStatus.STARTED;

        for(UUID uuid : seekers) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                seekers.remove(uuid);
                continue;
            }

            if(player.isLocalPlayer()) {
                for (UUID hiderUuid : hiders) {
                    Player hider = Bukkit.getPlayer(hiderUuid);
                    if (hider == null) {
                        hiders.remove(hiderUuid);
                        continue;
                    }

                    player.showPlayer(EventSpigot.getInstance(), hider);
                }
            }
        }

        if(!remote) {
            countdown = Bukkit.getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
                final Component component = Component.text("Time remaining: ").color(NamedTextColor.GREEN)
                        .append(Component.text(StringUtils.formatSeconds(counter))).color(NamedTextColor.YELLOW);

                if(counter < 0) {
                    this.runEnd();
                    return;
                }

                for(UUID uuid : getAllPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null) {
                        hiders.remove(uuid);
                        continue;
                    }

                    player.sendActionBar(component);
                }

                counter--;
            }, 0, 20);
        }
    }

    public void runEnd() {
        MultiLib.notify("eventspigot:hns", "end");
        this.end(false);
    }

    public void end(boolean remote) {
        if (status != GameStatus.STARTED)
            return;
        this.status = GameStatus.ENDED;

        for(Player player : Bukkit.getLocalOnlinePlayers()) {
            for(Player player1 : Bukkit.getAllOnlinePlayers()) {
                player.showPlayer(EventSpigot.getInstance(), player1);
            }
        }

        if(countdown != null)
            countdown.cancel();

        if (!remote) {
            if(hiders.size() == 0) {
                Bukkit.broadcast(Component.text("The seekers win!").color(NamedTextColor.GREEN));
            } else {
                Bukkit.broadcast(Component.text("The hiders win!").color(NamedTextColor.GREEN));
            }
        }
    }

    public GameStatus getStatus(){
        return this.status;
    }
}
