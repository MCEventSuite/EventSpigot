package dev.imabad.mceventsuite.spigot.interactions;

public enum Interaction {

  NONE(""),
  RIGHT_CLICK_BLOCK("Right click block"),
  RIGHT_CLICK_AIR("Right click air"),
  RIGHT_CLICK("Right click anything"),
  LEFT_CLICK_BLOCK("Left click block"),
  LEFT_CLICK_AIR("Left click air"),
  LEFT_CLICK("Left click anything"),
  LEFT_CLICK_ENTITY("Left Click entity"),
  RIGHT_CLICK_ENTITY("Right click entity"),
  INTERACT_ENTITY("Interact with entity"),
  CLICK_INSIDE_INVENTORY("Click inside inventory"),
  CLOSE_INVENTORY("Close inventory"),
  DRAG_INVENTORY("Drag inventory"),
  MOVE("move");

  Interaction(String name) { }
}
