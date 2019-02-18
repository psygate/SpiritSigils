package net.civex4.spiritsigils.sigils.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RuneInventory extends ASigilInventory {
    public final static ItemStack LOCKED_SLOT_ITEMSTACK = new ItemStack(Material.REDSTONE_BLOCK, 1);

    public RuneInventory(SigilInventoryManager sigilInventoryManager) {
        super(sigilInventoryManager, calculateChestInventorySize(sigilInventoryManager.getSigil().getSigilSetting().getMaxAttunedPlayers()), "Runes");

        int maxrunes = sigilInventoryManager.getSigil().getSigilSetting().getMaxRunes();
        addListener(ev -> {
            if (ev.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                ev.setCancelled(false);
            }
        });
        if (getInventory().getSize() > maxrunes) {
            for (int i = maxrunes; i < getInventory().getSize(); i++) {
                addListener(i, ev -> ev.setCancelled(true));
                getInventory().setItem(i, LOCKED_SLOT_ITEMSTACK);
            }
        }

        for (int i = 0; i < maxrunes; i++) {
            addListener(i, ev -> placeRuneOrCancel(ev));
        }
    }

    private void placeRuneOrCancel(InventoryClickEvent ev) {
        ev.setCancelled(true);
        ItemStack hand = ev.getCursor();
        ItemStack slot = getInventory().getItem(ev.getSlot());
        if (isEmpty(slot)) {
            if (!isEmpty(hand) && isValidRune(hand)) {
                ItemStack placement = hand.clone();
                placement.setAmount(1);
                hand.setAmount(hand.getAmount() - 1);
                ev.getWhoClicked().setItemOnCursor(hand.getAmount() > 0 ? hand : null);
                getInventory().setItem(ev.getSlot(), placement);
            }
        } else {
            if (!isEmpty(hand) && hand.isSimilar(slot) && hand.getAmount() < hand.getMaxStackSize()) {
                getInventory().setItem(ev.getSlot(), null);
                hand.setAmount(hand.getAmount() + 1);
                ev.getWhoClicked().setItemOnCursor(hand);
            } else if (isEmpty(hand) && !isEmpty(slot)) {
                getInventory().setItem(ev.getSlot(), null);
                ev.getWhoClicked().setItemOnCursor(slot);
            }
        }
    }


    private final boolean isValidRune(ItemStack stack) {
        return getSigilInventoryManager().getSigil().getSigilSetting().isValidRune(stack);
    }

    /**
     * Collect all runes as itemstacks. Collected runes have a guaranteed stack size of 1.
     *
     * @return
     */
    public Map<ItemStack, Integer> collectRunes() {
        Map<ItemStack, Integer> runes = new HashMap<>();

        for (ItemStack rune : getInventory()) {
            if (rune == null) continue;
            ItemStack crune = rune;

            if (rune.getAmount() != 1) {
                crune = rune.clone();
                crune.setAmount(1);
            }

            if (crune != null && !isEmpty(crune) && !crune.isSimilar(LOCKED_SLOT_ITEMSTACK)) {
                runes.compute(crune, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }

        return runes;
    }
}
