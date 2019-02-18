package net.civex4.spiritsigils.sigils.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class FuelInventory extends ASigilInventory {
    public final static int INDICATOR_SLOT = 0;
    public final static int FUEL_SLOT = 1;


    public FuelInventory(SigilInventoryManager sigilInventoryManager) {
        super(sigilInventoryManager, InventoryType.FURNACE, "Navigation");
        addListener(new PlayerInventoryListener() {
            @Override
            public void onClick(InventoryClickEvent ev) {
                if (ev.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    ev.setCancelled(false);
                }
            }
        });
        addListener(FUEL_SLOT, new SlotListener() {
            @Override
            public void onClick(InventoryClickEvent ev) {
                ev.setCancelled(true);
                if (ev.getClickedInventory() != null) {
                    int slotIdx = ev.getSlot();
                    Optional<ItemStack2> opt = recombineStacks(remapAction(ev), getInventory().getItem(ev.getSlot()), ev.getCursor(), FuelInventory.this::isFuel);
                    opt.ifPresent(stacks -> {
                        getInventory().setItem(slotIdx, stacks.target);
                        ev.getWhoClicked().setItemOnCursor(stacks.source);
                    });
                }
            }
        });
    }

    public void setFuelLevel(float ratio) {
        if (!getInventory().getViewers().isEmpty()) {
            int stackRatio = (int) (64 * ratio);

            if (stackRatio > 0) {
                ItemStack stack = new ItemStack(Material.LAVA_BUCKET, stackRatio);
                getInventory().setItem(INDICATOR_SLOT, stack);
            }
        }
    }

    private boolean isFuel(ItemStack stack) {
        return stack != null && getSigilInventoryManager().getSigil().getSigilSetting().getFuelItem().isSimilar(stack);
    }

    public boolean consumeFuelItem() {
        if (getInventory().getItem(FUEL_SLOT) != null && getSigilInventoryManager().getSigil().getSigilSetting().getFuelItem().isSimilar(getInventory().getItem(FUEL_SLOT))) {
            ItemStack stack = getInventory().getItem(FUEL_SLOT);
            if (stack.getAmount() > 1) {
                stack.setAmount(stack.getAmount() - 1);
                getInventory().setItem(FUEL_SLOT, stack);
            } else {
                getInventory().setItem(FUEL_SLOT, null);
            }

            return true;
        } else {
            getInventory().setItem(INDICATOR_SLOT, null);
            return false;
        }
    }
}
