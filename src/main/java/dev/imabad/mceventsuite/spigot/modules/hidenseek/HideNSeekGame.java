package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import com.github.puregero.multilib.MultiLib;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.WorldGuard;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassPlayer;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.EventScoreboard;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.ScoreboardModule;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class HideNSeekGame {

    public enum GameStatus {
        JOINING,
        WAITING,
        STARTED,
        ENDED;
    }

    private GameStatus status;
    private List<UUID> seekers;
    private List<UUID> hiders;

    private BukkitTask countdown;

    int counter;
    final int gameStartTime;
    final int waitingStartTime;
    private final EventScoreboard eventScoreboard;

    public HideNSeekGame(EventScoreboard eventScoreboard) {
        this(0, 0, 0, eventScoreboard);
    }

    public HideNSeekGame(int startCountdown, int duration, int warmup, EventScoreboard eventScoreboard){
        this.status = GameStatus.JOINING;
        this.seekers = new ArrayList<>();
        this.hiders = new ArrayList<>();
        this.counter = startCountdown + duration + warmup;
        this.gameStartTime = duration;
        this.waitingStartTime = duration + startCountdown;
        this.eventScoreboard = eventScoreboard;
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

        for (Player player : Bukkit.getAllOnlinePlayers()) {
            if (!hiders.contains(player.getUniqueId()) && !seekers.contains(player.getUniqueId()))
                seeker.hidePlayer(EventSpigot.getInstance(), player);
        }

        if (seeker.isLocalPlayer() && getStatus() == GameStatus.WAITING) {
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
        } else if(seeker.isLocalPlayer()) {
            seeker.sendMessage(ChatColor.GREEN + "You will be a seeker this round!");
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
            if(player.isLocalPlayer() && nameOfFinder != null) {
                player.showTitle(Title.title(
                        Component.text("You are now a Seeker!").color(NamedTextColor.GOLD),
                        Component.text("You were caught by ").color(NamedTextColor.WHITE)
                                .append(Component.text(nameOfFinder).color(NamedTextColor.GOLD))
                ));
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

        if (status == GameStatus.JOINING || status == GameStatus.WAITING) {
            this.hiders.add(uuid);

            for (Player player : Bukkit.getAllOnlinePlayers()) {
                if (!hiders.contains(player.getUniqueId()) && !seekers.contains(player.getUniqueId()))
                    hider.hidePlayer(EventSpigot.getInstance(), player);
            }

            if (status == GameStatus.WAITING) {
                if (hider.isLocalPlayer()) {
                    hider.sendMessage(ChatColor.GREEN + "YOU ARE NOW A HIDER!");
                    hider.sendMessage(ChatColor.YELLOW + "You are hidden from the seekers until the game begins!");
                    hider.sendMessage(ChatColor.RED + "You have " + Math.round((counter - gameStartTime) / 60) + " minutes to hide!");
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

    public int getActualTimeSeconds() {
        if(getStatus() == GameStatus.JOINING) {
            return this.counter - this.waitingStartTime;
        } else if(getStatus() == GameStatus.WAITING) {
            return this.counter - this.gameStartTime;
        } else {
            return this.counter;
        }
    }

    public void startCountdown() {
        countdown = Bukkit.getScheduler().runTaskTimer(EventSpigot.getInstance(), () -> {
            if(counter < 0) {
                this.runEnd();
                return;
            }

            if(counter == this.waitingStartTime && getStatus() == GameStatus.JOINING) {
                this.startWait(false);
                MultiLib.notify("eventspigot:hns", "startwait");
                return;
            }

            if(counter == this.gameStartTime && getStatus() == GameStatus.WAITING) {
                this.start(false);
                MultiLib.notify("eventspigot:hns", "start");
                return;
            }

            counter--;

            final int time = this.getActualTimeSeconds();
            final String text = getStatus() == GameStatus.JOINING ? "Waiting for Players" :
                    getStatus() == GameStatus.WAITING ? "Hiding Time Left" :
                            "Time Remaining";

            final Component component = Component.text(text + ": ").color(NamedTextColor.GREEN)
                    .append(Component.text(StringUtils.formatSeconds(time))).color(NamedTextColor.YELLOW);

            for(UUID uuid : getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    hiders.remove(uuid);
                    continue;
                }

                player.sendActionBar(component);
            }

            this.eventScoreboard.update();
        }, 0, 20);
    }

    public void startWait(boolean remote) {
        if(this.status != GameStatus.JOINING)
            return;

        if(!remote) {
            int seekersRequired = this.hiders.size() < 5 ? 1 : this.hiders.size() < 10 ? 2 : 3;
            if (this.seekers.size() < seekersRequired) {
                seekersRequired = seekersRequired - this.seekers.size();
                Random random = new Random();
                for (int i = 0; i < seekersRequired; i++) {
                    if (hiders.size() <= 1)
                        break;
                    final UUID hider = this.hiders.get(random.nextInt(this.hiders.size() - 1));
                    this.hiders.remove(hider);
                    this.seekers.add(hider);
                    MultiLib.notify("eventspigot:hns", "silentset:seekers:" + hider);
                }
            }
        }

        for(Player player : Bukkit.getLocalOnlinePlayers()) {
            if(hiders.contains(player.getUniqueId())) {
                player.showTitle(Title.title(
                        Component.text("You are a ").color(NamedTextColor.GRAY)
                                .append(Component.text("Hider").color(NamedTextColor.BLUE)),
                        Component.text("Stay hidden from the seekers!").color(NamedTextColor.WHITE)
                ));
                player.getScoreboard().getTeam("Hider").addPlayer(player);
                player.teleport(new Location(
                        Bukkit.getWorld("world"), 0.5, 30, 8.5, 0, 0));
            } else if(seekers.contains(player.getUniqueId())) {
                player.showTitle(Title.title(
                        Component.text("You are a ").color(NamedTextColor.GRAY)
                                .append(Component.text("Seeker").color(NamedTextColor.GOLD)),
                        Component.text("Find all the hiders!").color(NamedTextColor.WHITE)
                ));
                player.getScoreboard().getTeam("Seeker").addPlayer(player);
                player.teleport(new Location(
                        Bukkit.getWorld("world"), 0.5, 30, 8.5, 0, 0));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        Integer.MAX_VALUE,
                        1,
                        false,
                        false
                ));
                for(Player otherPlayer : Bukkit.getAllOnlinePlayers()) {
                    if(!seekers.contains(otherPlayer.getUniqueId()))
                        player.hidePlayer(EventSpigot.getInstance(), otherPlayer);
                }
            }
        }

        this.status = GameStatus.WAITING;
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

        for(Player player : Bukkit.getLocalOnlinePlayers()) {
            if(getAllPlayers().contains(player.getUniqueId())) {
                player.showTitle(Title.title(
                        Component.text("The game has started!").color(NamedTextColor.GREEN),
                        Component.text("All hiders are ").color(NamedTextColor.WHITE)
                                .append(Component.text("VISIBLE!").color(NamedTextColor.GREEN).decorate(
                                        TextDecoration.BOLD,
                                        TextDecoration.UNDERLINED
                                ))
                ));
            }
        }
    }

    public void runEnd() {
        MultiLib.notify("eventspigot:hns", "end");
        this.end(false);
    }

    public void end(boolean remote) {
        if (status == GameStatus.ENDED || status == GameStatus.JOINING)
            return;
        this.status = GameStatus.ENDED;

        for(Player player : Bukkit.getLocalOnlinePlayers()) {
            for(Player player1 : Bukkit.getAllOnlinePlayers()) {
                player.showPlayer(EventSpigot.getInstance(), player1);
            }

            if(player.getScoreboard().equals(this.eventScoreboard.getScoreboard())) {
                player.setScoreboard(EventSpigot.getInstance().getScoreboard());
            }
        }

        boolean stoppedByAdmin = false;
        boolean hidersWin = false;
        if(counter > 0 && hiders.size() > 0)
            stoppedByAdmin = true;
        else if(counter <= 0 && hiders.size() > 0)
            hidersWin = true;

        if(countdown != null)
            countdown.cancel();

        if (!remote) {
            final PlayerDAO playerDAO = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class)
                    .getMySQLDatabase().getDAO(PlayerDAO.class);
            final EventPassModule eventPassModule = EventCore.getInstance().getModuleRegistry().getModule(EventPassModule.class);

            if(stoppedByAdmin) {
                final Title title = Title.title(
                        Component.text("Game Over").color(NamedTextColor.RED),
                        Component.text("Ended by an Admin").color(NamedTextColor.WHITE)
                );
                for(Player player : Bukkit.getAllOnlinePlayers()) {
                    player.showTitle(title);
                }
            } else {
                String winners = "seeker";
                Component component = Component.text("Seekers").color(NamedTextColor.GOLD);
                List<UUID> winnersUUIDs = seekers;
                String reason = "The last hider was found";

                if(hidersWin) {
                    winnersUUIDs = hiders;
                    component = Component.text("Hiders").color(NamedTextColor.BLUE);
                    winners = "hider";
                    reason = "Seekers ran out of time";
                }

                for(UUID uuid : winnersUUIDs) {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null)
                        continue;
                    EventPlayer eventPlayer = playerDAO.getPlayer(uuid);
                    if(eventPlayer == null)
                        continue;
                    eventPassModule.awardXP(eventPlayer, 1000, player, "Winning Hide & Seek as a " + winners, true);
                }

                final Title title = Title.title(
                        component.append(Component.text(" Win!").color(NamedTextColor.GREEN)),
                        Component.text(reason)
                );

                for(Player player : Bukkit.getAllOnlinePlayers()) {
                    player.showTitle(title);
                }
            }
        }
    }

    public GameStatus getStatus(){
        return this.status;
    }
}
