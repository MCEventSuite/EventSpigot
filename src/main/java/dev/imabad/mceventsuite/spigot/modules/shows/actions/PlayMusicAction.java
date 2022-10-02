package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;

public class PlayMusicAction extends ShowAction{

    public int record;

    public PlayMusicAction(Show show, long time, int musicRecord) {
        super(show, time);
        record = musicRecord;
    }

    @Override
    public void execute() {
        //TODO: Play the record.
    }
}
