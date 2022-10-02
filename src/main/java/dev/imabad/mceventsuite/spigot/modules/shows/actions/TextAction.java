package dev.imabad.mceventsuite.spigot.modules.shows.actions;

import dev.imabad.mceventsuite.spigot.modules.shows.Show;

public class TextAction extends ShowAction {

    private String text;

    public TextAction(Show show, long time, String textToShow) {
        super(show, time);
        this.text = textToShow;
    }

    @Override
    public void execute() {
        this.getShow().displayText(text);
    }
}
