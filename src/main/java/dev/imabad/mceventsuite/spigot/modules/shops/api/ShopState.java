package dev.imabad.mceventsuite.spigot.modules.shops.api;

public enum ShopState {
    OPEN(true),
    CLOSE(false);

    private boolean open;

    ShopState(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
