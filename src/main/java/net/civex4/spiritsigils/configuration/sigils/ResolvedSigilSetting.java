package net.civex4.spiritsigils.configuration.sigils;

import net.civex4.spiritsigils.configuration.runes.RuneSetting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ResolvedSigilSetting {
    private String sigilName;
    private ItemStack placementItem;
    private Material blockType;
    private int maxAttunedPlayers;
    private int maxRunes;
    private ItemStack fuelItem;
    private int ticksPerFuelUnit;
    private List<RuneSetting> runeSettings;
    private int maxRange;

    public ResolvedSigilSetting(String sigilName, ItemStack placementItem, Material blockType, int maxAttunedPlayers, int maxRunes, ItemStack fuelItem, int ticksPerFuelUnit, int maxRange, List<RuneSetting> runeLimits) {
        this.sigilName = sigilName;
        this.placementItem = placementItem;
        this.blockType = blockType;
        this.maxAttunedPlayers = maxAttunedPlayers;
        this.maxRunes = maxRunes;
        this.fuelItem = fuelItem;
        this.ticksPerFuelUnit = ticksPerFuelUnit;
        this.runeSettings = runeLimits;
        this.maxRange = maxRange;
    }

    public ResolvedSigilSetting(SigilSetting sigilSetting, List<RuneSetting> runeLimits) {
        this(
                sigilSetting.getSigilName(),
                sigilSetting.getPlacementItem().toItemStack(),
                sigilSetting.getBlockType(),
                sigilSetting.getMaxAttunedPlayers(),
                sigilSetting.getMaxRunes(),
                sigilSetting.getFuelItem().toItemStack(),
                sigilSetting.getTicksPerFuelUnit(),
                sigilSetting.getMaxRange(),
                runeLimits
        );
    }

    public String getSigilName() {
        return sigilName;
    }

    public void setSigilName(String sigilName) {
        this.sigilName = sigilName;
    }

    public ItemStack getPlacementItem() {
        return placementItem;
    }

    public void setPlacementItem(ItemStack placementItem) {
        this.placementItem = placementItem;
    }

    public Material getBlockType() {
        return blockType;
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

    public ItemStack getFuelItem() {
        return fuelItem;
    }

    public void setFuelItem(ItemStack fuelItem) {
        this.fuelItem = fuelItem;
    }

    public int getTicksPerFuelUnit() {
        return ticksPerFuelUnit;
    }

    public void setTicksPerFuelUnit(int ticksPerFuelUnit) {
        this.ticksPerFuelUnit = ticksPerFuelUnit;
    }

    public boolean isValidRune(ItemStack stack) {
        return runeSettings.stream().anyMatch(ev -> ev.getItem().isSimilar(stack));
    }

    public List<RuneSetting> getRuneSettings() {
        return runeSettings;
    }

    public void setRuneSettings(List<RuneSetting> runeSettings) {
        this.runeSettings = runeSettings;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }
}
