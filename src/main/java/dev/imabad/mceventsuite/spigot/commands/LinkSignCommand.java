package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkSignCommand extends BaseCommand {

    public LinkSignCommand() {
        super("linksign", "eventsuite.linksign");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(super.execute(sender, label, args)) {
            if(!(sender instanceof Player player)) {
                return false;
            }
            if(args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Incorrect Usage: /linksign <link> <text>");
                return false;
            }
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    EventSpigot.getInstance().reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                    return false;
                }else if(args[0].equalsIgnoreCase("transfer")) {
                    if(player.getTargetBlockExact(5) != null) {
                        Block block = player.getTargetBlockExact(5);
                        if(block.getState() instanceof Sign sign) {
                            SignBlockEntity tileEntitySign = (SignBlockEntity) ((CraftWorld) block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));

                            if(tileEntitySign != null) {
                                for(Component message : tileEntitySign.messages) {
                                    if(message.getStyle().getClickEvent() != null) {
                                        var clickEvent = message.getStyle().getClickEvent();
                                        String url = clickEvent.getValue();
//                                        String text = ComponentSerializer.toString(message);
//                                        text = text.substring(2, text.length() - 2);
                                        String[] split = url.split(" ");
                                        List<String> part_split = new ArrayList<>();
                                        StringBuilder builder = new StringBuilder();
                                        for(String s : split) {
                                            if(!s.startsWith("slink")) {
                                                part_split.add(s);
                                            }
                                        }
                                        String newUrl = part_split.remove(0);
                                        part_split.forEach(s -> builder.append(s).append(" "));
                                        String value = "<hover:show_text:'<green>Click to open in browser'><click:open_url:'" + newUrl + "'>" + builder.toString().trim() + "</click></hover>";
                                        if(EventSpigot.getInstance().getConfig().isConfigurationSection("signs")) {
                                            EventSpigot.getInstance().getConfig().getConfigurationSection("signs").set(block.getX() + ";" + block.getY() + ";" + block.getZ(), value);
                                        }else{
                                            EventSpigot.getInstance().getConfig().createSection("signs");
                                            EventSpigot.getInstance().getConfig().getConfigurationSection("signs").set(block.getX() + ";" + block.getY() + ";" + block.getZ(), value);
                                        }
                                        EventSpigot.getInstance().saveConfig();

                                        String previousText = sign.getLine(0);
                                        TextComponent newLine;
                                        if(previousText.length() > 0) {
                                            newLine = new TextComponent(previousText);
                                        }else{
                                            newLine = new TextComponent();
                                        }
                                        newLine.setClickEvent(null);
                                        tileEntitySign.setMessage(0, Component.Serializer.fromJson(ComponentSerializer.toString(newLine)));

                                        player.sendMessage(ChatColor.GREEN + "Converted - URL: " + newUrl + " Text: " + builder);
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
            }else{
                Block block = player.getTargetBlockExact(5);
                if(block == null) {
                    sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
                    return false;
                }
                if(!(block.getState() instanceof Sign)) {
                    sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
                    return false;
                }

                if(EventSpigot.getInstance().getConfig().isConfigurationSection("signs")) {
                    EventSpigot.getInstance().getConfig().getConfigurationSection("signs").set(block.getX() + ";" + block.getY() + ";" + block.getZ(), "<hover:show_text:'<green>Click to open in browser'><click:open_url:'" + args[0] + "'>" + getLastArgs(args, 1) + "</click></hover>");
                }else{
                    EventSpigot.getInstance().getConfig().createSection("signs");
                    EventSpigot.getInstance().getConfig().getConfigurationSection("signs").set(block.getX() + ";" + block.getY() + ";" + block.getZ(), "<hover:show_text:'<green>Click to open in browser'><click:open_url:'" + args[0] + "'>" + getLastArgs(args, 1) + "</click></hover>");
                }
                EventSpigot.getInstance().saveConfig();
                SignBlockEntity tileEntitySign = (SignBlockEntity) ((CraftWorld) block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));

                Sign sign = (Sign) block.getState();
                String previousText = sign.getLine(0);
                TextComponent newLine;
                if(previousText.length() > 0) {
                    newLine = new TextComponent(previousText);
                }else{
                    newLine = new TextComponent();
                }
                newLine.setClickEvent(null);
                tileEntitySign.setMessage(0, Component.Serializer.fromJson(ComponentSerializer.toString(newLine)));
                sender.sendMessage(ChatColor.GREEN + "DONE!");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
