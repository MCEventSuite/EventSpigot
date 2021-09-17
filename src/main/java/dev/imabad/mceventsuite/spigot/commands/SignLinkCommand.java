package dev.imabad.mceventsuite.spigot.commands;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SignLinkCommand extends BaseCommand{
    public SignLinkCommand() {
        super("slink");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        String link = args[0];
        String text = getLastArgs(args, 1);
        Player player = (Player) sender;
        BaseComponent[] textComponent = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        for (BaseComponent baseComponent : textComponent) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        }
        EventSpigot.getInstance().getAudiences().player(player).openBook(Book.builder().addPage(
                BungeeComponentSerializer.get().deserialize(textComponent)).author(Component.text("Cubed! 2021")).build());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
