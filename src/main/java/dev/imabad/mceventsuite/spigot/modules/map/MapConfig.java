package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.api.BaseConfig;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Kevin;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Tree;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MapConfig extends BaseConfig {

    private List<Kevin> kevins = new ArrayList<>();
    private List<Tree> trees = new ArrayList<>();

    public List<Kevin> getKevins() {
        return kevins;
    }

    public List<Tree> getTrees() { return trees; }

    @Override
    public String getName() {
        return "map";
    }
}
