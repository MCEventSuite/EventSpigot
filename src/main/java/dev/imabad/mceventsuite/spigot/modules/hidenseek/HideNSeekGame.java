package dev.imabad.mceventsuite.spigot.modules.hidenseek;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HideNSeekGame {

    enum GameStatus {
        WAITING,
        STARTED,
        ENDED;
    }

    private GameStatus status;
    private List<UUID> seekers;
    private List<UUID> hiders;

    public HideNSeekGame(UUID starter){
        this.status = GameStatus.WAITING;
        this.seekers = new ArrayList<>();
        this.seekers.add(starter);
        this.hiders = new ArrayList<>();
    }

    public void join(UUID uuid){
        if(status == GameStatus.WAITING){
            this.hiders.add(uuid);
        }
    }

    public void leave(UUID uuid){
        this.hiders.remove(uuid);
    }

    public GameStatus getStatus(){
        return this.status;
    }

    public void start(){

    }

}
