package dev.imabad.mceventsuite.spigot.modules.scoreboards;

import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Row {
    private String title;
    private RowRender render;
    private Map<UUID, String> getLastRender;

    public Row(String title, RowRender render) {
        this.title = StringUtils.colorizeMessage(title);
        this.render = render;
        this.getLastRender = new HashMap<>();
    }

    public void clear(UUID uuid) {
        this.getLastRender.remove(uuid);
    }

    public String getTitle() {
        return this.title;
    }
    public String render(Player player) {
        final String string = StringUtils.colorizeMessage(this.render.render(player));
        this.getLastRender.put(player.getUniqueId(), string);
        return string;
    }

    public String getLastRender(Player player) {
        return this.getLastRender.get(player.getUniqueId());
    }

    public void forceLastRender(Player player, String string) {
        this.getLastRender.put(player.getUniqueId(), string);
    }

    public static abstract class RowRender {
        public abstract String render(Player player);
    }
}
