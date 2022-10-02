package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;

public abstract class ShowAction {

    private Show show;
    private long time;

    public ShowAction(Show show, long time){
        this.show = show;
        this.time = time;
    }

    public abstract void execute();

    public Show getShow(){
        return show;
    }

}
