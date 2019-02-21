package net.civex4.spiritsigils.sigils.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AttunementInventory extends ASigilInventory {
    private final static ItemStack EMPTY_ATTUNEMENT_SLOT_ITEM;

    static {
        EMPTY_ATTUNEMENT_SLOT_ITEM = new ItemStack(Material.ENDER_PEARL, 1);
        ItemMeta meta = EMPTY_ATTUNEMENT_SLOT_ITEM.getItemMeta();
        meta.setDisplayName("Empty Attunement Slot");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Click here to attune to this sigil."));
        EMPTY_ATTUNEMENT_SLOT_ITEM.setItemMeta(meta);
    }

    public AttunementInventory(SigilInventoryManager sigilInventoryManager) {
        super(sigilInventoryManager, calculateChestInventorySize(sigilInventoryManager.getSigil().getSigilSetting().getMaxAttunedPlayers()), "Attunement");

        int maxattunements = sigilInventoryManager.getSigil().getSigilSetting().getMaxAttunedPlayers();
        for (int i = 0; i < maxattunements; i++) {
            getInventory().setItem(i, EMPTY_ATTUNEMENT_SLOT_ITEM.clone());
        }

        addListener(ev -> {
            if (ev.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                ev.setCancelled(false);
            }
        });

        for (int i = 0; i < maxattunements; i++) {
            addListener(i, ev -> attunePlayer(ev));
        }
    }

    private void attunePlayer(InventoryClickEvent ev) {
        ItemStack item = getInventory().getItem(ev.getSlot());
        if (!isAttunedItem(item)) {
            if (!getSigilInventoryManager().getSigil().getSigilManager().isPlayerAttuned(ev.getWhoClicked().getUniqueId())) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + ev.getWhoClicked().getName());
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.setLore(Arrays.asList("Click here to remove your attunement to this sigil."));
                item.setItemMeta(meta);
                getInventory().setItem(ev.getSlot(), item);
                getSigilInventoryManager().getSigil().getSigilManager().attunePlayerToSigil(ev.getWhoClicked().getUniqueId(), getSigilInventoryManager().getSigil());
            } else {
                ev.getWhoClicked().sendMessage(ChatColor.RED + "You're already attuned to a sigil.");
            }
        } else if (isAttunedToPlayer(item, ev.getWhoClicked())) {
            getInventory().setItem(ev.getSlot(), EMPTY_ATTUNEMENT_SLOT_ITEM.clone());
            getSigilInventoryManager().getSigil().getSigilManager().unattunePlayer(ev.getWhoClicked().getUniqueId());
        }
    }

    public void unattunePlayer(Player p) {
        for (int i = 0; i < getInventory().getSize(); i++) {
            ItemStack stack = getInventory().getItem(i);
            if (!isEmpty(stack) && isAttunedToPlayer(stack, p)) {
                getInventory().setItem(i, EMPTY_ATTUNEMENT_SLOT_ITEM);
            }
        }
    }

    private boolean isAttunedToPlayer(ItemStack item, HumanEntity whoClicked) {
        return item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + whoClicked.getName());
    }

    private boolean isAttunedItem(ItemStack stack) {
        return !stack.getItemMeta().getEnchants().isEmpty()
                && stack.getItemMeta().getDisplayName() != null
                && !stack.getItemMeta().getDisplayName().isEmpty();
    }

}
