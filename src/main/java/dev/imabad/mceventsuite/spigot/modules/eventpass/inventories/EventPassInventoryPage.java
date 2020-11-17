package dev.imabad.mceventsuite.spigot.modules.eventpass.inventories;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.modules.eventpass.EventPassModule;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassDAO;
import dev.imabad.mceventsuite.core.modules.eventpass.db.EventPassPlayer;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.spigot.api.EventInventory;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EventPassInventoryPage extends EventInventory {

  private static final ItemStack LEFT_ARROW = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNzg0NTFiZjI2Y2Y0OWZkNWY1NGNkOGYyYjM3Y2QyNWM5MmU1Y2E3NjI5OGIzNjM0Y2I1NDFlOWFkODkifX19", "&cBack");
  private static final ItemStack RIGHT_ARROW = ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3ZjM2NjZkM2NlZGZhZTU3Nzc4Yzc4MjMwZDQ4MGM3MTlmZDVmNjVmZmEyYWQzMjU1Mzg1ZTQzM2I4NmUifX19", "&cNext");
  private static final ItemStack CLOSE = ItemUtils.createItemStack(Material.BARRIER, "&cClose");
  private static final ItemStack INFO = ItemUtils.createItemStack(Material.PAPER, "&cInfo");
  private static final ItemStack BLANK = ItemUtils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, "&c");

  private int page = 0;
  private int maxPage = 1;
  private int startLevel = 1;
  private int pageLevels = 29;

  private EventPlayer eventPlayer;
  private EventPassPlayer eventPassPlayer;

  public EventPassInventoryPage(Player player, EventPlayer eventPlayer, int page, int startLevel, int pageLevels, int maxPage) {
    super(player, "EventPass", 54);
    this.page = page;
    this.pageLevels = pageLevels;
    this.startLevel = startLevel;
    this.maxPage = maxPage;
    this.eventPlayer = eventPlayer;
    this.eventPassPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(
        EventPassDAO.class).getOrCreateEventPass(eventPlayer);
  }

  @Override
  protected void populate() {
    //Bottom Row
    bottomRow();
    generatePath();
  }

  public Material getItemForLevel(int level){
    return this.eventPassPlayer.levelFromXP() >= level ? Material.LIME_STAINED_GLASS_PANE : this.eventPassPlayer.levelFromXP() + 1 == level ? Material.ORANGE_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
  }

  public String getLevelName(int level){
    ChatColor textColor = this.eventPassPlayer.levelFromXP() >= level ? ChatColor.GREEN : this.eventPassPlayer.levelFromXP() + 1 == level ? ChatColor.GOLD : ChatColor.RED;
    return textColor + "Level " + level;
  }

  public void generatePath(){
    int currentLevel = this.eventPassPlayer.levelFromXP();
    int startPos = 0;
    boolean isAcross = true;
    boolean isDown;
    int nextPos = startPos;
    int verticalCount = 0;
    this.inventory.setItem(0, ItemUtils.createItemStack(getItemForLevel(startLevel), getLevelName(startLevel)));
    nextPos++;
    int levelVerticalCount = 0;
    int levelHorizontalCount = 2;
    for(int level = startLevel + 1; level < startLevel + pageLevels; level++){
      this.inventory.setItem(nextPos, ItemUtils.createItemStack(getItemForLevel(level), getLevelName(level)));
      if(levelHorizontalCount == 2){
        isAcross = false;
        levelHorizontalCount = 0;
      } else if(levelVerticalCount == 4){
        verticalCount++;
        isAcross = true;
        levelVerticalCount = 0;
      }
      isDown = verticalCount % 2 == 0;
      if(isAcross){
        nextPos = nextPos + 1;
        levelHorizontalCount++;
      } else {
        levelVerticalCount++;
        if(isDown) {
          nextPos = nextPos + 9;
        } else {
          nextPos = nextPos - 9;
        }
      }
    }
    for(int i = 0; i < 54; i++){
      if(this.inventory.getItem(i) == null || this.inventory.getItem(i).getType() == Material.AIR){
        this.inventory.setItem(i, BLANK);
      }
    }
  }

  public void bottomRow(){
    ItemStack BLANK = ItemUtils.createItemStack(Material.WHITE_STAINED_GLASS_PANE, "&l");
    for(int i = 45; i < 54; i++){
      this.inventory.setItem(i, BLANK);
    }
    if(page > 0){
      this.inventory.setItem(48, LEFT_ARROW);
    }
    if(page < maxPage){
      this.inventory.setItem(50, RIGHT_ARROW);
    }
    this.inventory.setItem(49, CLOSE);
    this.inventory.setItem(53, INFO);
  }

  @Override
  public boolean onPlayerClick(HumanEntity whoClicked, int slot, boolean isPlayerInventory,
      @Nullable ItemStack clickItem, SlotType slotType, ClickType clickType) {
    if(slot == 48 && page > 0 && backInventory != null){
      backInventory.open((Player) whoClicked, null);
    } else if (slot == 50){
      int newPage = this.page + 1;
      new EventPassInventoryPage((Player) whoClicked, eventPlayer, newPage, this.pageLevels * newPage + 1, this.pageLevels, this.maxPage).open((Player) whoClicked, this);
    } else if (slot == 49){
      whoClicked.closeInventory();
    }
    return true;
  }
}
