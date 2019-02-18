package net.civex4.spiritsigils.configuration.runes;

import net.civex4.spiritsigils.configuration.ItemStackProxy;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PotionEffectRuneSetting extends RuneSetting implements TickingRuneEffect {
    private String potionEffectType;
    private int strength;
    private int duration;

    public PotionEffectRuneSetting() {
    }

    public PotionEffectRuneSetting(String runeName, ItemStackProxy item, int maximumRunesPerSigil, String potionEffectType, int strength, int duration) {
        super(runeName, item, maximumRunesPerSigil);
        this.potionEffectType = potionEffectType;
        this.strength = strength;
        this.duration = duration;
    }

    @Override
    public void apply(int amountOfRunes, List<Player> players) {
        int intensity = strength * Math.min(amountOfRunes, getMaximumRunesPerSigil());
        players.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potionEffectType), getDuration(), intensity)));
    }

    public String getPotionEffectType() {
        return potionEffectType;
    }

    public void setPotionEffectType(String potionEffectType) {
        this.potionEffectType = potionEffectType;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
