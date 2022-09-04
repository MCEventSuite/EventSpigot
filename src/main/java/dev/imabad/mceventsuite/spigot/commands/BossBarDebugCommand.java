package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.spigot.modules.bossbar.BossBarConfig;
import dev.imabad.mceventsuite.spigot.modules.bossbar.BossBarModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BossBarDebugCommand extends BaseCommand {
    public BossBarDebugCommand() {
        super("bbd", "eventsuite.bbd");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final BossBarConfig.Event event = new BossBarConfig.Event("Game Server Panel", BossBarConfig.Event.Location.MAIN_STAGE,
                System.currentTimeMillis() - (60 * 1000 * 25), System.currentTimeMillis() + (60 * 1000 * 5));
        final BossBarConfig.Event sideEvent = new BossBarConfig.Event("Community Panel", BossBarConfig.Event.Location.SIDE_STAGE,
                System.currentTimeMillis() - (60 * 1000 * 2), System.currentTimeMillis() + (60 * 1000 * 28));
        final BossBarConfig.Event nextEventOne = new BossBarConfig.Event("Twitter Panel", BossBarConfig.Event.Location.MAIN_STAGE,
                System.currentTimeMillis() + (60 * 1000 * 5), System.currentTimeMillis() + (60 * 1000 * 35));
        final BossBarConfig.Event nextEventTwo = new BossBarConfig.Event("Hide and Seek", BossBarConfig.Event.Location.OUTSIDE,
                System.currentTimeMillis() + (60 * 1000 * 2), System.currentTimeMillis() + (60 * 1000 * 22));

        BossBarModule module = EventCore.getInstance().getModuleRegistry().getModule(BossBarModule.class);
        module.getConfig().getEvents().clear();
        module.getConfig().getEvents().add(event);
        module.getConfig().getEvents().add(sideEvent);
        module.getConfig().getEvents().add(nextEventOne);
        module.getConfig().getEvents().add(nextEventTwo);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
