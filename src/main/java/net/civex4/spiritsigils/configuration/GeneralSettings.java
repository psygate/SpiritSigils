package net.civex4.spiritsigils.configuration;

public class GeneralSettings {
    private long sigilTickDelay = 20;

    public GeneralSettings() {
    }

    public GeneralSettings(long sigilTickDelay) {
        this.sigilTickDelay = sigilTickDelay;
    }

    public long getSigilTickDelay() {
        return sigilTickDelay;
    }

    public void setSigilTickDelay(long sigilTickDelay) {
        this.sigilTickDelay = sigilTickDelay;
    }
}
