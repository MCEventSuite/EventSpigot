package dev.imabad.mceventsuite.spigot.modules.bossbar;

import dev.imabad.mceventsuite.core.api.BaseConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BossBarConfig extends BaseConfig {

    private List<Event> events;
    private List<String> texts;

    @Override
    public String getName() {
        return "bossbar";
    }

    public List<String> getText() {
        return texts;
    }
    public List<Event> getEvents() {
        return events;
    }

    public List<Event> getCurrentEvents() {
        long currentTime = System.currentTimeMillis();
        final List<Event> eventsList = new ArrayList<>();
        for(Event event : getEvents()) {
            if(event.start <= currentTime && event.end >= currentTime)
                eventsList.add(event);
        }
        return eventsList;
    }

    public List<Event> getSoonEvents() {
        long currentTime = System.currentTimeMillis();
        List<Event> startingSoon = new ArrayList<>();
        for(Event event : getEvents()) {
            if(event.start > currentTime && (event.start - currentTime <= (60 * 1000 * 10)))
                startingSoon.add(event);
        }
        return startingSoon;
    }

    public static class Event {
        public String name;
        public Location location;
        public long start;
        public long end;

        public Event(String name, Location location, long start, long end) {
            this.name = name;
            this.location = location;
            this.start = start;
            this.end = end;
        }

        public double getProgress() {
            final long time = System.currentTimeMillis();
            if(time > start) {
                double progress = ((double) time - (double) start) / ((double) end - (double) start);
                System.out.println((double) time + " - " + (double) start + " / " + (double)end + " = " + progress);
                return progress;
            }

            double timeLeft = (double) start - (double) System.currentTimeMillis();
            return timeLeft / (((double) 60 * 1000 * 10));
        }

        public static enum Location {
            MAIN_STAGE("Main Stage"), SIDE_STAGE("Outdoor Stage"), OUTSIDE("Outside"), NONE("None");

            private String human;

            Location(String name) {
                this.human = name;
            }

            public String getHumanName() {
                return this.human;
            }
        }
    }
}
