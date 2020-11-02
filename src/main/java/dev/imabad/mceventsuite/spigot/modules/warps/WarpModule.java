package dev.imabad.mceventsuite.spigot.modules.warps;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.events.CoreEvent;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventBooth;
import dev.imabad.mceventsuite.core.api.objects.EventBoothPlot;
import dev.imabad.mceventsuite.core.modules.mysql.dao.BoothDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.modules.warps.commands.WarpCommand;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WarpModule extends Module {

    private List<EventBoothPlot> plots;
    private List<WarpItem> warpItems;

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public void onEnable() {
        SimpleCommandMap commandMap = EventSpigot.getInstance().getCommandMap();
        commandMap.register("warp", new WarpCommand());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, this::onMysqlLoad);
    }

    private void onMysqlLoad(MySQLLoadedEvent t) {
        plots = t.getMySQLDatabase().getDAO(BoothDAO.class).getPlots();
        generateWarpItems();
    }

    public void generateWarpItems(){
        warpItems = new ArrayList<>();
        World world = Bukkit.getWorld("world");
        plots.sort(Comparator.comparingInt(o -> WarpCategory.fromName(o.getBoothType()).ordinal()));
        for(int i = 0; i < plots.size(); i++){
            EventBoothPlot boothPlot = plots.get(i);
            WarpCategory fi = WarpCategory.fromName(boothPlot.getBoothType());
            String name = boothPlot.getBooth() == null ? "Booth" : boothPlot.getBooth().getName();
            ItemStack item = ItemUtils.createItemStack(fi.stackColor, StringUtils.colorizeMessage("&r&l" + name), 1);
            String[] splits = boothPlot.getFrontPos() == null ? boothPlot.getPosOne().split(",") : boothPlot.getFrontPos().split(",");
            Location l = new Location(world, Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
            warpItems.add(new WarpItem(name, item, l, fi));
        }
        ItemStack STAGE_ITEM = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY5ODIxNzcyY2EyNjczZjRhY2I1MzkzZDEyNmIyZTYyZTgyY2U4NTVhNDljZmVlYTc3ODMwYzVkMTI0YSJ9fX0=", "&r&9&lStage");
        warpItems.add(new WarpItem("Stage", STAGE_ITEM, new Location(world, 926,71, 529), WarpCategory.OTHER));
        ItemStack GAMES_ITEM = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM0Mjk3ZDE5YTFhNzEzOTExNDdhNjljODI1ZDM3NDgyMThlNGM1YmQwMTZjN2NjYWNjYjA1ZmUzZjQifX19", "&r&9&lGames");
        warpItems.add(new WarpItem("Games", GAMES_ITEM, new Location(world, 977, 72, 535), WarpCategory.OTHER));
        ItemStack STICKY_PISTON = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdlMWE0YmRmYWI2ZjM5OTA2MTAxZDI5MmQzYmYyMjg4ZWJkNTUzZDFkZGEzYTNhNzgyMjUwNzRhYmM1NThmNiJ9fX0=","&r&a&lStickyPiston");
        warpItems.add(new WarpItem("StickyPiston", STICKY_PISTON, new Location(world, 926, 71, 538), WarpCategory.OTHER));
        ItemStack ENTRANCE = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgwOWZhZTBjMTI5YTAxYTBhNDBlYTNiNTZlODA2ZTk2MmFkMjE1NjkyMGI2OWE1MDhmNDI3YWYyMjI4OTA2ZSJ9fX0=", "&r&9&lEntrance");
        warpItems.add(new WarpItem("Entrance", ENTRANCE, new Location(world, 408, 78, 538), WarpCategory.OTHER));
        ItemStack STATION = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQyMDcwYWNjODE0YmM5NDZlNTk4NzllYzdkYTQ1ZGU5ODRkM2VlOWExNTkzOTNkZWZiNTk4NTNhYmUzYjYifX19", "&r&9&lStation");
        warpItems.add(new WarpItem("Station", STATION, new Location(world, 408, 78, 538), WarpCategory.OTHER));
    }

    public List<WarpItem> getWarpItems() {
        return warpItems;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<? extends Module>> getDependencies() {
        return Collections.emptyList();
    }

    public List<EventBoothPlot> getPlots() {
        return plots;
    }
}
