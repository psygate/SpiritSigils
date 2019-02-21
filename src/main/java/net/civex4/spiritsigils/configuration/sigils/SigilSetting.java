package net.civex4.spiritsigils.configuration.sigils;

import net.civex4.spiritsigils.configuration.ItemStackProxy;
import org.bukkit.Material;

import java.util.Map;

public class SigilSetting {
    private String sigilName;
    private ItemStackProxy placementItem;
    private Material blockType;
    private int maxAttunedPlayers;
    private int maxRunes;
    private ItemStackProxy fuelItem;
    private int ticksPerFuelUnit;
    private Map<String, Integer> runeLimits;
    private int maxRange;

    public SigilSetting() {
    }

    public SigilSetting(String sigilName, ItemStackProxy placementItem, Material blockType, int maxAttunedPlayers, int maxRunes, ItemStackProxy fuelItem, int ticksPerFuelUnit, Map<String, Integer> runeLimits, int maxRange) {
        this.sigilName = sigilName;
        this.placementItem = placementItem;
        this.blockType = blockType;
        this.maxAttunedPlayers = maxAttunedPlayers;
        this.maxRunes = maxRunes;
        this.fuelItem = fuelItem;
        this.ticksPerFuelUnit = ticksPerFuelUnit;
        this.runeLimits = runeLimits;
        this.maxRange = maxRange;
    }

    public ItemStackProxy getPlacementItem() {
        return placementItem;
    }

    public void setPlacementItem(ItemStackProxy placementItem) {
        this.placementItem = placementItem;
    }

    public Material getBlockType() {
        return blockType;
    }

    public String getSigilName() {
        return sigilName;
    }

    public void setSigilName(String sigilName) {
        this.sigilName = sigilName;
    }

    public void setBlockType(Material blockType) {
        this.blockType = blockType;
    }

    public int getMaxAttunedPlayers() {
        return maxAttunedPlayers;
    }

    public void setMaxAttunedPlayers(int maxAttunedPlayers) {
        this.maxAttunedPlayers = maxAttunedPlayers;
    }

    public int getMaxRunes() {
        return maxRunes;
    }

    public void setMaxRunes(int maxRunes) {
        this.maxRunes = maxRunes;
    }

    public ItemStackProxy getFuelItem() {
        return fuelItem;
    }

    public void setFuelItem(ItemStackProxy fuelItem) {
        this.fuelItem = fuelItem;
    }

    public int getTicksPerFuelUnit() {
        return ticksPerFuelUnit;
    }

    public void setTicksPerFuelUnit(int ticksPerFuelUnit) {
        this.ticksPerFuelUnit = ticksPerFuelUnit;
    }

    public Map<String, Integer> getRuneLimits() {
        return runeLimits;
    }

    public void setRuneLimits(Map<String, Integer> runeLimits) {
        this.runeLimits = runeLimits;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }
}

