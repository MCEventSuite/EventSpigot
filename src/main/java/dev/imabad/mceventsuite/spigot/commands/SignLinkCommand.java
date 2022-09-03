package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SignLinkCommand extends BaseCommand{
    public SignLinkCommand() {
        super("slink");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player))
            return false;

        String link = args[0];
        String text = getLastArgs(args, 1);

        final ClickEvent clickEvent = ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link);

        Component textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.translateAlternateColorCodes('&', text));
        textComponent = textComponent.clickEvent(clickEvent);
        textComponent = textComponent.hoverEvent(HoverEvent.showText(Component.text("Click to open in browser").color(NamedTextColor.GREEN)));

        playerAudience(((Player)sender)).sendMessage(textComponent);


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
