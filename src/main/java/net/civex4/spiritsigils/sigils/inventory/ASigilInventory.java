package net.civex4.spiritsigils.sigils.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public abstract class ASigilInventory implements InventoryHolder {
    private final SigilInventoryManager sigilInventoryManager;
    private final Inventory inventory;
    private final Map<Integer, List<SlotListener>> slotListeners = new HashMap<>();
    private final List<PlayerInventoryListener> playerInventoryListeners = new LinkedList<>();

    public ASigilInventory(SigilInventoryManager sim, InventoryType type) {
        inventory = Bukkit.createInventory(this, type);
        sigilInventoryManager = Objects.requireNonNull(sim);
    }

    public ASigilInventory(SigilInventoryManager sim, InventoryType type, String title) {
        inventory = Bukkit.createInventory(this, type, title);
        sigilInventoryManager = Objects.requireNonNull(sim);
    }

    public ASigilInventory(SigilInventoryManager sim, int size) {
        inventory = Bukkit.createInventory(this, size);
        sigilInventoryManager = Objects.requireNonNull(sim);
    }

    public ASigilInventory(SigilInventoryManager sim, int size, String title) {
        inventory = Bukkit.createInventory(this, size, title);
        sigilInventoryManager = Objects.requireNonNull(sim);
    }

    public SigilInventoryManager getSigilInventoryManager() {
        return sigilInventoryManager;
    }

    public void addListener(int slot, SlotListener sil) {
        slotListeners.putIfAbsent(slot, new LinkedList<>());
        slotListeners.get(slot).add(sil);
    }

    public void addListener(PlayerInventoryListener sil) {
        playerInventoryListeners.add(sil);
    }

    final static int calculateChestInventorySize(int size) {
        return ((size - 1) / 9 + 1) * 9;
    }
    public void onPlayerInteractEvent(InventoryClickEvent ev) {
        ev.setCancelled(true);

        if (ev.getClick() == ClickType.RIGHT && ev.getClickedInventory() == null) {
            getSigilInventoryManager().getNavigationInventory().presentTo(ev.getWhoClicked());
        } else {
            if (isLocalInventory(ev.getClickedInventory())) {
                for (SlotListener sl : slotListeners.getOrDefault(ev.getSlot(), Collections.emptyList())) {
                    sl.onClick(ev);
                }
            }

            if (isPlayerInventory(ev.getClickedInventory())) {
                for (PlayerInventoryListener pil : playerInventoryListeners) {
                    pil.onClick(ev);
                }
            }
        }
    }

    public final Optional<ItemStack2> recombineStacks(InventoryAction action, ItemStack slot, ItemStack hand) {
        return recombineStacks(action, slot, hand, (a) -> true);
    }

    private final static String rs(ItemStack st) {
        return st != null ? (st.getType() + "/" + st.getAmount()) : "null";
    }

    final static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

    private final static boolean isPlacementAction(InventoryAction action) {
        return Arrays.asList(InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE).contains(action);
    }

    private final static boolean isPickupAction(InventoryAction action) {
        return Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE).contains(action);
    }

    private final static ItemStack cloneFirstIfNotAirOrOther(ItemStack a, ItemStack b, int size) {
        if (a == null || a.getType() == Material.AIR) {
            ItemStack c = b.clone();
            c.setAmount(size);
            return c;
        } else {
            ItemStack c = a.clone();
            c.setAmount(size);
            return c;
        }
    }

    private final int cutSizeByActionType(InventoryAction action, int stackSize) {
        switch (action) {
            case PLACE_ALL:
            case PICKUP_ALL:
                return stackSize;
            case PLACE_SOME:
            case PICKUP_HALF:
            case PICKUP_SOME:
                return stackSize / 2;
            case PLACE_ONE:
            case PICKUP_ONE:
                return 1;
            default:
                throw new UnsupportedOperationException("Unknown action: " + action);
        }
    }

    private final static int determineMaxStackSize(ItemStack a, ItemStack b) {
        assert !(a == null && b == null);
        assert !(a.getType() == Material.AIR && b.getType() == Material.AIR);

        if (a != null && a.getType() != Material.AIR) {
            return a.getType().getMaxStackSize();
        } else {
            return b.getType().getMaxStackSize();
        }
    }

    private final ItemStack2 combineStacks(InventoryAction action, ItemStack target, ItemStack source) {
        assert action != null;
        assert target != null;
        assert source != null;
        assert !(source.getType() == Material.AIR && target.getType() == Material.AIR);
        assert !(source.getAmount() == 0 && target.getAmount() == 0);
        assert isPickupAction(action) || isPlacementAction(action);

        int maxStackSize = determineMaxStackSize(target, source);
        if (isPlacementAction(action)) {
            int cutSize = cutSizeByActionType(action, source.getAmount());

            int targetSize = Math.min(maxStackSize, target.getAmount() + cutSize);
            int sourceSize = target.getAmount() + source.getAmount() - targetSize;

            ItemStack targetCpy = cloneFirstIfNotAirOrOther(target, source, targetSize);
            ItemStack sourceCpy = cloneFirstIfNotAirOrOther(source, target, sourceSize);

            return new ItemStack2(targetCpy, sourceCpy);
        } else if (isPickupAction(action)) {
            int cutSize = cutSizeByActionType(action, target.getAmount());

            int sourceSize = Math.min(maxStackSize, source.getAmount() + cutSize);
            int targetSize = target.getAmount() + source.getAmount() - sourceSize;

            ItemStack targetCpy = cloneFirstIfNotAirOrOther(target, source, targetSize);
            ItemStack sourceCpy = cloneFirstIfNotAirOrOther(source, target, sourceSize);

            return new ItemStack2(targetCpy, sourceCpy);
        } else {
            throw new IllegalStateException();
        }
    }

    public final InventoryAction remapAction(InventoryClickEvent ev) {
        assert ev.getInventory() != null && ev.getClickedInventory() != null;
        ItemStack hand = ev.getCursor();
        ItemStack slot = ev.getInventory().getItem(ev.getSlot());

        if (ev.getClick().isLeftClick()) {
            if (isEmpty(hand)) {
                if (isEmpty(slot)) {
                    return InventoryAction.NOTHING;
                } else {
                    return InventoryAction.PICKUP_ALL;
                }
            } else {
                if (isEmpty(slot)) {
                    return InventoryAction.PLACE_ALL;
                } else {
                    return InventoryAction.PLACE_ALL;
                }
            }
        } else if (ev.getClick().isRightClick()) {
            if (isEmpty(hand)) {
                if (isEmpty(slot)) {
                    return InventoryAction.NOTHING;
                } else {
                    return InventoryAction.PICKUP_HALF;
                }
            } else {
                if (isEmpty(slot)) {
                    return InventoryAction.PLACE_ONE;
                } else {
                    return InventoryAction.PLACE_ONE;
                }
            }
        }

        return InventoryAction.NOTHING;
    }

    public final Optional<ItemStack2> recombineStacks(InventoryAction action, ItemStack target, ItemStack source, Function<ItemStack, Boolean> canPlaceCallback) {
        if (isEmpty(source) && isEmpty(target)) {
            return Optional.empty(); // Both empty, nothing to do.
        } else if (isEmpty(source) && !isEmpty(target)) {
            return Optional.of(combineStacks(action, target, source));
        } else {
            if (canPlaceCallback.apply(source)) {
                if (isEmpty(target)) {
                    return Optional.of(new ItemStack2(source.clone(), null));
                } else {
                    return Optional.of(combineStacks(action, target, source));
                }
            } else {
                return Optional.empty();    // Can't place item, nothing to do.
            }
        }
    }

    public void onPlayerInteractEvent(InventoryDragEvent ev) {
        ev.setCancelled(true);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void clear() {
        inventory.clear();
        slotListeners.clear();
        playerInventoryListeners.clear();
    }

    public void presentTo(HumanEntity player) {
        player.openInventory(getInventory());
    }

    protected final boolean isItemPlacement(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME;
    }

    protected final boolean isPlayerInventory(Inventory inv) {
        return inv != null && !getInventory().equals(inv);
    }

    protected final boolean isLocalInventory(Inventory inv) {
        return inv != null && getInventory().equals(inv);
    }

    public final static class ItemStack2 {
        public final ItemStack target, source;

        public ItemStack2(ItemStack slot, ItemStack hand) {
            this.target = slot;
            this.source = hand;
        }

        public ItemStack getTarget() {
            return target;
        }

        public ItemStack getSource() {
            return source;
        }

        public final boolean isEmpty() {
            return ASigilInventory.isEmpty(target) && ASigilInventory.isEmpty(source);
        }
    }
}
