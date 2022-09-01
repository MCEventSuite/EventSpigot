package dev.imabad.mceventsuite.spigot.interactions;


import org.bukkit.event.Event;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class InteractionRegistry {

  private static final ConcurrentHashMap<dev.imabad.mceventsuite.spigot.interactions.Interaction, Queue<Consumer<Event>>> interactions = new ConcurrentHashMap<>();

  public static void registerInteraction(dev.imabad.mceventsuite.spigot.interactions.Interaction type, Consumer<Event> eventConsumer) {
    if(interactions.containsKey(type)){
      Queue<Consumer<Event>> consumers = interactions.get(type);
      consumers.add(eventConsumer);
      interactions.replace(type, consumers);
    }else{
      Queue<Consumer<Event>> consumers = new ConcurrentLinkedQueue<>();
      consumers.add(eventConsumer);
      interactions.put(type, consumers);
    }
  }

  public static void handleEvent(Interaction type, Event event) {
    if(!interactions.containsKey(type)){
      return;
    }
    Queue<Consumer<Event>> interactionsToHandle = interactions.get(type);
    for (Consumer<Event> runnable : interactionsToHandle) {
      runnable.accept(event);
    }
  }

  public static void clear(){
    interactions.clear();
  }

}
