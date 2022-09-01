package dev.imabad.mceventsuite.spigot.commands;

import com.sk89q.worldedit.blocks.SignBlock;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LinkSignCommand extends BaseCommand{

    public LinkSignCommand() {
        super("linksign", "eventsuite.linksign");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(super.execute(sender, label, args)){
            if(!(sender instanceof Player)){
                return false;
            }
            if(args.length == 0){
                sender.sendMessage(ChatColor.RED + "Incorrect Usage: /linksign <link> <text>");
                return false;
            }
            Player player = (Player)sender;
            Block block = player.getTargetBlockExact(5);
            if(block == null){
                sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
                return false;
            }
            if(!(block.getState() instanceof Sign)){
                sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
                return false;
            }
            ;
            SignBlockEntity tileEntitySign = (SignBlockEntity)((CraftWorld)block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));

            Sign sign = (Sign)block.getState();
            String link = args[0];
            String text = getLastArgs(args, 1);
            String previousText = sign.getLine(0);
            TextComponent newLine;
            if(previousText.length() > 0){
                newLine = new TextComponent(previousText);
            } else {
                newLine = new TextComponent();
            }
            newLine.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "slink " + link + " " + text));
//            tileEntitySign.setMessage(0, Component.Serializer.fromJson(ComponentSerializer.toString(newLine)));
            sender.sendMessage(ChatColor.GREEN + "DONE!");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
