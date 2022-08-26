package dev.imabad.mceventsuite.spigot.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SignLinkCommand extends BaseCommand{
    public SignLinkCommand() {
        super("slink");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        String link = args[0];
        String text = getLastArgs(args, 1);
        BaseComponent[] textComponent = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
        for (BaseComponent baseComponent : textComponent) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        }
//        sender.sendMessage(textComponent);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
