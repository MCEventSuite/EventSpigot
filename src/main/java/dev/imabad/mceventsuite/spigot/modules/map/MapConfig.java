package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.core.api.BaseConfig;
import dev.imabad.mceventsuite.spigot.modules.map.objects.Kevin;

import java.util.ArrayList;
import java.util.List;

public class MapConfig extends BaseConfig {

    private List<Kevin> kevins = new ArrayList<>();

    public List<Kevin> getKevins() {
        return kevins;
    }

    @Override
    public String getName() {
        return "map";
    }
}
