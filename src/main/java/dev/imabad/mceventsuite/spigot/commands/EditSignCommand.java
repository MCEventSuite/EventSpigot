package dev.imabad.mceventsuite.spigot.commands;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditSignCommand extends BaseCommand {
    public EditSignCommand() {
        super("editsign", "eventsuite.editsign");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!super.execute(sender, label, args)){
            return false;
        }
        if(!(sender instanceof Player)){
            return false;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.RED + "Incorrect Usage: /editsign <set|clear> <line> <text>");
            return false;
        }
        Player player = (Player)sender;
        String action = args[0];
        Block block = player.getTargetBlockExact(5);
        if(block == null){
            sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
            return false;
        }
        if(!(block.getState() instanceof Sign)){
            sender.sendMessage(ChatColor.RED + "You are not looking at a sign!");
            return false;
        }
        if(EventCore.getInstance().getModuleRegistry().isModuleEnabled(BoothModule.class)){
            if(!EventCore.getInstance().getModuleRegistry().getModule(BoothModule.class).canPlayerEdit(player, block.getLocation())){
                sender.sendMessage(ChatColor.RED + "You cannot edit that sign.");
                return false;
            }
        }
        Sign sign = (Sign)block.getState();
        if(args.length == 1 && action.equalsIgnoreCase("clear")){
            for(int i = 0; i < 4; i++){
                sign.setLine(i, "");
            }
            sign.update();
            sender.sendMessage(ChatColor.GREEN + "Cleared sign.");
        } else if(args.length == 2 && action.equalsIgnoreCase("clear")) {
            int line = Integer.parseInt(args[1]);
            if(line < 1 || line > 4){
                sender.sendMessage(ChatColor.RED + "Invalid line number, must be between 1 and 4.");
                return false;
            }
            sign.setLine(line - 1, "");
            sign.update();
            sender.sendMessage(ChatColor.GREEN + "Cleared line.");
        } else if(args.length >= 3 && action.equalsIgnoreCase("set")){
            int line = Integer.parseInt(args[1]);
            if(line < 1 || line > 4){
                sender.sendMessage(ChatColor.RED + "Invalid line number, must be between 1 and 4.");
                return false;
            }
            String text = getLastArgs(args, 2);
            String formattedText = StringUtils.formatRGB(text);
            sign.setLine(line, formattedText);
            sign.update();
            sender.sendMessage(ChatColor.GREEN + "Updated line.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            return Arrays.asList("set", "clear");
        } else if(args.length == 2){
            return Arrays.asList("1", "2", "3", "4");
        }
        return Collections.emptyList();
    }
}
