package net.civex4.spiritsigils.runes;

import org.bukkit.inventory.ItemStack;

public class Rune {
    private ItemStack runeItem;

    public Rune() {
    }

    public Rune(ItemStack runeItem) {
        this.runeItem = runeItem;
    }

    public ItemStack getRuneItem() {
        return runeItem;
    }

    public void setRuneItem(ItemStack runeItem) {
        this.runeItem = runeItem;
    }
}
