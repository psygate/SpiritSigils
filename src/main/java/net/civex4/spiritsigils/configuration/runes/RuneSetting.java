package net.civex4.spiritsigils.configuration.runes;

import net.civex4.spiritsigils.configuration.ItemStackProxy;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class RuneSetting {
    private String runeName;
    private ItemStackProxy item;
    private int maximumRunesPerSigil;

    public RuneSetting() {
    }

    public RuneSetting(String runeName, ItemStackProxy item, int maximumRunesPerSigil) {
        this.runeName = runeName;
        this.item = item;
        this.maximumRunesPerSigil = maximumRunesPerSigil;
    }

    public String getRuneName() {
        return runeName;
    }

    public void setRuneName(String runeName) {
        this.runeName = runeName;
    }

    public ItemStackProxy getItem() {
        return item;
    }

    public void setItem(ItemStackProxy item) {
        this.item = item;
    }

    public int getMaximumRunesPerSigil() {
        return maximumRunesPerSigil;
    }

    public void setMaximumRunesPerSigil(int maximumRunesPerSigil) {
        this.maximumRunesPerSigil = maximumRunesPerSigil;
    }

    public abstract void apply(int amountOfRunes, List<Player> players);
}
