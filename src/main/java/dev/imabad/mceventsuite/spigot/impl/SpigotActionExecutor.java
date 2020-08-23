package dev.imabad.mceventsuite.spigot.impl;

import dev.imabad.mceventsuite.core.api.actions.Action;
import dev.imabad.mceventsuite.core.api.actions.IActionExecutor;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpigotActionExecutor implements IActionExecutor {
    @Override
    public void execute(Action action, EventPlayer player) {
        switch(action.getType()){
            case GIVE_ITEM: {
                Player spigotPlayer = Bukkit.getPlayer(player.getUUID());
                Material material = Material.matchMaterial((String) action.getVariable("material"));
                if (material == null) {
                    System.out.println("[EventSpigot] Tried to execute give item action but could not find material. Material: " + action.getVariable("material"));
                    return;
                }
                int slot = (int) action.getVariable("slot");
                ItemStack itemStack = new ItemStack(material, (int) action.getVariable("amount"));
                String name = (String) action.getVariable("name");
                List<String> lore = (List) action.getVariable("lore");
                ItemMeta meta = itemStack.getItemMeta();
                if (name.length() > 0) {
                    meta.setDisplayName(name);
                }
                if (lore.size() > 0) {
                    meta.setLore(lore);
                }
                itemStack.setItemMeta(meta);
                if (slot != -1) {
                    spigotPlayer.getInventory().setItem(slot, itemStack);
                } else {
                    spigotPlayer.getInventory().addItem(itemStack);
                }
            }

        }
    }
}
