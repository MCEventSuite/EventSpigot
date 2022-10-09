package dev.imabad.mceventsuite.spigot;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;
import dev.imabad.mceventsuite.core.modules.join.JoinModule;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.core.modules.mysql.dao.RankDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.core.modules.redis.RedisMessageListener;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.events.RedisConnectionEvent;
import dev.imabad.mceventsuite.core.modules.redis.messages.DonationMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.UpdatePlayerXPMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.UpdatedPlayerMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.UpdatedRankMessage;
import dev.imabad.mceventsuite.core.modules.scavenger.ScavengerModule;
import dev.imabad.mceventsuite.core.modules.servers.ServersModule;
import dev.imabad.mceventsuite.core.modules.servers.objects.Server;
import dev.imabad.mceventsuite.spigot.commands.*;
import dev.imabad.mceventsuite.spigot.impl.EventPermissible;
import dev.imabad.mceventsuite.spigot.impl.EventPermission;
import dev.imabad.mceventsuite.spigot.impl.SpigotActionExecutor;
import dev.imabad.mceventsuite.spigot.listeners.BuildListener;
import dev.imabad.mceventsuite.spigot.listeners.EventListener;
import dev.imabad.mceventsuite.spigot.listeners.PlayerListener;
import dev.imabad.mceventsuite.spigot.modules.bedrock.BedrockModule;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import dev.imabad.mceventsuite.spigot.modules.bossbar.BossBarModule;
import dev.imabad.mceventsuite.spigot.modules.bubbles.BubbleModule;
import dev.imabad.mceventsuite.spigot.modules.chat.ChatFilterModule;
import dev.imabad.mceventsuite.spigot.modules.daylight.DaylightModule;
import dev.imabad.mceventsuite.spigot.modules.eventblocker.EventBlockModule;
import dev.imabad.mceventsuite.spigot.modules.eventpass.EventPassSpigotModule;
import dev.imabad.mceventsuite.spigot.modules.hidenseek.HideNSeekModule;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import dev.imabad.mceventsuite.spigot.modules.meet.MeetModule;
import dev.imabad.mceventsuite.spigot.modules.minecon.MineconModule;
import dev.imabad.mceventsuite.spigot.modules.npc.NPCModule;
import dev.imabad.mceventsuite.spigot.modules.player.PlayerModule;
import dev.imabad.mceventsuite.spigot.modules.scavengers.ScavengerHuntSpigotModule;
import dev.imabad.mceventsuite.spigot.modules.scoreboards.ScoreboardModule;
import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.stafftrack.StaffTrackModule;
import dev.imabad.mceventsuite.spigot.modules.stage.StageModule;
import dev.imabad.mceventsuite.spigot.modules.teams.TeamManager;
import dev.imabad.mceventsuite.spigot.modules.teams.TeamModule;
import dev.imabad.mceventsuite.spigot.modules.warps.WarpModule;
import dev.imabad.mceventsuite.spigot.utils.PermissibleInjector;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EventSpigot extends JavaPlugin {

    private static EventSpigot eventSpigot;

    public static EventSpigot getInstance(){
        return eventSpigot;
    }

    private List<EventRank> ranks;
    private HashMap<UUID, PermissionAttachment> permissionAttachments;
    private HashMap<Integer, Team> rankTeams = new HashMap<>();
    private Scoreboard scoreboard;
    private SimpleCommandMap commandMap;
    private boolean isEvent = false;
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        eventSpigot = this;
        new EventCore(getDataFolder());
        EventCore.getInstance().getEventRegistry().registerListener(RedisConnectionEvent.class, redisConnectionEvent -> {
            System.out.println("[EventSpigot] Connected to Redis");
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(UpdatedPlayerMessage.class, new RedisMessageListener<>((msg) -> {
                if(EventCore.getInstance().getEventPlayerManager().hasPlayer(msg.getUUID())){
                    EventRank previousRank = EventCore.getInstance().getEventPlayerManager().getPlayer(msg.getUUID()).get().getRank();;
                    EventPlayer eventPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).getPlayer(msg.getUUID());
                    EventCore.getInstance().getEventPlayerManager().addPlayer(eventPlayer);
                    Player player = getServer().getPlayer(eventPlayer.getUUID());
                    if(player != null){
                        EventPermissible eventPermissible = new EventPermissible(player, eventPlayer);
                        try {
                            PermissibleInjector.inject(player, eventPermissible);
                        } catch (Exception ignored) {
                        }

                        TeamManager teamManager = EventCore.getInstance().getModuleRegistry().getModule(TeamModule.class)
                                .getTeamManager();
                        teamManager.removePlayerFromTeam(player, false);
                        teamManager.addPlayerToTeam(player, eventPlayer.getRank());
                    }
                }
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(UpdatePlayerXPMessage.class, new RedisMessageListener<>((msg) -> {
                if(EventCore.getInstance().getEventPlayerManager().hasPlayer(msg.getUuid())){
                    Player player = Bukkit.getPlayer(msg.getUuid());
                    if(player != null) {
                        int level = msg.getNewLevel();
                        float progressToNext = (float) EventPassModule.experience(level) / (float) EventPassModule.experience(level + 1);
                        player.setLevel(level);
                        player.setExp(progressToNext);
                        getAudiences().player(player).playSound(Sound.sound(Key.key("minecraft:ui.toast.challenge_complete"), Sound.Source.AMBIENT, 1f, 1f));
                    }
                }
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(DonationMessage.class, new RedisMessageListener<>((msg) -> {
                if(EventCore.getInstance().getIdentifier().equalsIgnoreCase("shard1") && msg.isPlantTree())
                    EventCore.getInstance().getModuleRegistry().getModule(MapModule.class)
                            .spawnTree(msg.getUsername());
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(UpdatedRankMessage.class, new RedisMessageListener<>((msg) -> {
                ranks = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRanks(true);
                EventCore.getInstance().getEventPlayerManager().getPlayers().stream().filter(player -> player.getRank().getId() == msg.getRank().getId()).forEach(player -> {
                    player.setRank(msg.getRank());
                    Player bPlayer = getServer().getPlayer(player.getUUID());
                    if(bPlayer != null) {
                        bPlayer.recalculatePermissions();

                        TeamManager teamManager = EventCore.getInstance().getModuleRegistry().getModule(TeamModule.class)
                                .getTeamManager();
                        teamManager.removePlayerFromTeam(bPlayer, false);
                        teamManager.addPlayerToTeam(bPlayer, msg.getRank());
                    }
                });
            }));
        });
        audiences = BukkitAudiences.create(this);
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventSpigotModule());
        ServersModule serversModule = new ServersModule();
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(serversModule);
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventBlockModule());
        EventCore.getInstance().setActionExecutor(new SpigotActionExecutor());
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new JoinModule());
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new ChatFilterModule());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, (event) -> {
            ranks = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRanks();
        });
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BuildListener(), this);
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            System.out.println("Vault is enabled, registering permission handler");
            getServer().getServicesManager().register(Permission.class, new EventPermission(), this, ServicePriority.Highest);
            System.out.println("Did we register successfully? " +  getServer().getServicesManager().isProvidedFor(Permission.class));
            System.out.println("Type of permission provider: " + getServer().getServicesManager().load(Permission.class).getName());
        }
        commandMap = getCommandMap();
        if(commandMap != null){
            commandMap.register("nv", new NightVisionToggle());
            commandMap.register("editsign", new EditSignCommand());
            commandMap.register("speed", new SpeedCommand());
            commandMap.register("linksign", new LinkSignCommand());
            commandMap.register("slink", new SignLinkCommand());
            commandMap.register("tpa", new TpaCommand());
            commandMap.register("tpaccept", new TpacceptCommand());
            commandMap.register("tptoggle", new TptoggleCommand());
            commandMap.register("fly", new FlyCommand());
            commandMap.register("bbd", new BossBarDebugCommand());
        }
        if(getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BoothModule());
        } else {
            getServer().getPluginManager().registerEvents(new EventListener(), this);
            isEvent = true;
        }
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new MapModule());
        if(isEvent){
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new DaylightModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new WarpModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new ShopsModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new StaffTrackModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventPassModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventPassSpigotModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new ScavengerModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new ScavengerHuntSpigotModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new StageModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new PlayerModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BedrockModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BossBarModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new HideNSeekModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new NPCModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new ScoreboardModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new MineconModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new MeetModule());
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BubbleModule());
        }
        permissionAttachments = new HashMap<>();
        unRegisterBukkitCommand(getCommand("ban"));
        unRegisterBukkitCommand(getCommand("kick"));
        getServer().getScheduler().runTaskLaterAsynchronously(getInstance(), () -> {
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new TeamModule());
        }, 20 * 5);
        if(serversModule.getServerRedisManager().getServer(EventCore.getInstance().getIdentifier()) != null) {
            serversModule.getServerRedisManager().removeServer(EventCore.getInstance().getIdentifier());
            getLogger().info("Deleting server so we can be re-registered..");
        }
        getServer().getScheduler().runTaskTimerAsynchronously(getInstance(), () -> {
            Server thisServer = serversModule.getServerRedisManager().getServer(EventCore.getInstance().getIdentifier());

            if (thisServer == null) {
                System.out.println("[EventSpigot] Registering server...");
                thisServer = new Server(EventCore.getInstance().getIdentifier(), Bukkit.getIp().isBlank() ? "127.0.0.1" : Bukkit.getIp(), Bukkit.getPort(), 0, 0, 100);
            }

            thisServer.setPlayerCount(getServer().getOnlinePlayers().size());
            if(!thisServer.isOnline()){
                thisServer.setOnline(true);
            }

            serversModule.getServerRedisManager().addServer(thisServer);
        }, 0, 15 * 20);
    }

    public HashMap<Integer, Team> getRankTeams() {
        return rankTeams;
    }

    public List<EventRank> getRanks() {
        return ranks;
    }

    public Scoreboard getScoreboard() {
        if(scoreboard == null){
            scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        }
        return scoreboard;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    private Object getPrivateField(Object object, String field)throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public SimpleCommandMap getCommandMap(){
        if(commandMap != null){
            return commandMap;
        }
        try {
            Object result = getPrivateField(getServer().getPluginManager(), "commandMap");
            return (SimpleCommandMap) result;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void unRegisterBukkitCommand(PluginCommand cmd) {
        try {
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases()){
                if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())){
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onDisable() {
        Server server = EventCore.getInstance().getModuleRegistry().getModule(ServersModule.class).getServerRedisManager().getServer(EventCore.getInstance().getIdentifier());
        if(server != null){
            server.setOnline(false);
            server.setPlayerCount(0);
            EventCore.getInstance().getModuleRegistry().getModule(ServersModule.class).getServerRedisManager().addServer(server);
        }
        EventCore.getInstance().shutdown();
    }

    public HashMap<UUID, PermissionAttachment> getPermissionAttachments() {
        return permissionAttachments;
    }
}
