package dev.imabad.mceventsuite.spigot.modules.map.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.spigot.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NewBoothPlotCommand extends BaseCommand {

    //Usage: /nbp
    public NewBoothPlotCommand() {
        super("nbp");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        if(!sender.hasPermission("eventsuite.nbp")){
            return false;
        }
        if(args.length != 0){
            sender.sendMessage(ChatColor.RED + "Usage: /nbp");
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ensure you have a WorldEdit region selected!");
            return false;
        }
        Player player = (Player) sender;
        World world = player.getWorld();
        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(sender.getName());
        if(session == null){
            return false;
        }
        Region region;
        try {
            region = session.getSelection(BukkitAdapter.adapt(world));
        } catch (Exception e){
            sender.sendMessage(ChatColor.RED + "Please select a region");
            return false;
        }
        if(region == null){
            sender.sendMessage(ChatColor.RED + "Please select a region");
            return false;
        }
        int width = region.getWidth();
        int length = region.getLength();
        if(width != length){
            sender.sendMessage(ChatColor.RED + "Invalid region size");
            return false;
        }
        String posOne = region.getMinimumPoint().toParserString();
        String posTwo = region.getMaximumPoint().toParserString();
        String type;
        if(width == 17){
            type = "small";
        } else if(width == 37){
            type = "medium";
        } else if(width == 53){
            type = "large";
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid region size");
            return false;
        }
        EventBoothPlot newPlot = new EventBoothPlot(type, posOne, posTwo);
        EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(BoothDAO.class).saveBoothPlot(newPlot);
        sender.sendMessage(ChatColor.GREEN + "Created new plot");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
