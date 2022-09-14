package dev.imabad.mceventsuite.spigot.modules.eventpass;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.modules.redis.RedisChannel;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.messages.players.AwardPlayerXPMessage;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import java.awt.Color;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;

public class XPCannonCommand extends BaseCommand {
    public XPCannonCommand() {
        super("xpc");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("eventsuite.xpc")){
            return false;
        }
//        int countdown = 10;
//        int radius = 10;
//        int xp = 250;
//        if(args.length >= 1){
//            countdown = Integer.parseInt(args[0]);
//        }
//        if(args.length >= 2){
//            radius = Integer.parseInt(args[1]);
//        }
//        if(args.length == 3){
//            xp = Integer.parseInt(args[2]);
//        }
//        final int countDownTime = countdown;
//        final int finalRadius = radius;
//        final int xpAmount = xp;
//        Location location = player.getLocation();
//        Location primedTNT = location.clone();
//        Location hologramL = location.clone();
//        primedTNT.add(0.5, 0.5, 0.5);
//        hologramL.add(0.5, 2, 0.5);
//        Hologram hologram = HologramsAPI.createHologram(EventSpigot.getInstance(), hologramL);
//        final TextLine countdownLine = hologram.appendTextLine(ChatColor.RED + "" + ChatColor.BOLD + countdown);
//        hologram.appendTextLine(ChatColor.YELLOW + "" + ChatColor.BOLD + "XP Cannon");
//        TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(primedTNT, EntityType.PRIMED_TNT);
//        tntPrimed.setIsIncendiary(false);
//        tntPrimed.setFuseTicks(20 * countdown);
//        location.add(0.5, 0.5, 0.5);
//        new BukkitRunnable() {
//            int counter = 0;
//            @Override
//            public void run() {
//                counter++;
//                countdownLine.setText(ChatColor.RED + "" + ChatColor.BOLD + (countDownTime - counter));
//                for(int i = 0; i < 360; i++){
//                    double radians = Math.toRadians(i);
//                    double x = finalRadius * Math.cos(radians);
//                    double z = finalRadius * Math.sin(radians);
//                    location.add(x, 0, z);
//                    ParticleEffect.FLAME.display(location);
//                    location.subtract(x, 0, z);
//                }
//                location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.AMBIENT,1 ,1);
//                location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.AMBIENT,1 ,1);
//                if(counter >= countDownTime){
//                    cancel();
//                    ParticleEffect.EXPLOSION_HUGE.display(location);
//                    hologram.delete();
//                    location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT,1 ,1);
//                    location.getWorld().getNearbyEntitiesByType(Player.class, location, finalRadius).stream().filter(player1 -> !CitizensAPI.getNPCRegistry().isNPC(player1)).forEach(player1 -> EventCore.getInstance().getModuleRegistry().getModule(
//                        RedisModule.class).publishMessage(RedisChannel.GLOBAL, new AwardPlayerXPMessage(player1.getUniqueId(), xpAmount, "Earned from XP Cannon!")));
//                    new BukkitRunnable(){
//                        float r = 0;
//                        final float per3Ticks = finalRadius / 20f;
//                        @Override
//                        public void run() {
//                            r += per3Ticks;
//                            for (int i = 0; i < 360; i++) {
//                                double radians = Math.toRadians(i);
//                                double x = r * Math.cos(radians);
//                                double z = r * Math.sin(radians);
//                                location.add(x, 0, z);
//                                ParticleEffect.REDSTONE.display(location, new RegularColor(Color.YELLOW));
//                                location.subtract(x, 0, z);
//                            }
//                            if(r >= finalRadius){
//                                cancel();
//                            }
//                        }
//                    }.runTaskTimer(EventSpigot.getInstance(), 0, 1);
//                }
//            }
//        }.runTaskTimer(EventSpigot.getInstance(), 0, 20);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
