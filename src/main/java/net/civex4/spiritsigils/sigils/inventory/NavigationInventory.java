package net.civex4.spiritsigils.sigils.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NavigationInventory extends ASigilInventory {
    private final Map<ItemStack, ASigilInventory> inventories = new LinkedHashMap<>();

//    public static final ItemStack[] navigationItems = new ItemStack[]{
//            new ItemStackProxy(
//                    Material.FURNACE,
//                    1,
//                    ChatColor.DARK_PURPLE + "Fuel Inventory",
//                    Arrays.asList(ChatColor.WHITE + "Click here to go to the fuel menu of this sigil."),
//                    new HashMap<String, Integer>() {{
//                        put(Enchantment.DURABILITY.getName(), 1);
//                    }},
//                    Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
//            ).toItemStack(),
//            new ItemStackProxy(
//                    Material.DIAMOND,
//                    1,
//                    ChatColor.DARK_PURPLE + "Rune Inventory",
//                    Arrays.asList(ChatColor.WHITE + "Click here to go to the rune menu of this sigil."),
//                    new HashMap<String, Integer>() {{
//                        put(Enchantment.DURABILITY.getName(), 1);
//                    }},
//                    Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
//            ).toItemStack(),
//            new ItemStackProxy(
//                    Material.ENDER_PEARL,
//                    1,
//                    ChatColor.DARK_PURPLE + "Attunement Inventory",
//                    Arrays.asList(ChatColor.WHITE + "Click here to go to the attunement menu of this sigil."),
//                    new HashMap<String, Integer>() {{
//                        put(Enchantment.DURABILITY.getName(), 1);
//                    }},
//                    Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
//            ).toItemStack()
//    };

    public NavigationInventory(SigilInventoryManager sigilInventoryManager) {
        super(sigilInventoryManager, 9, "Navigation");
    }

    public void addInventory(ItemStack representationStack, ASigilInventory inventory) {
        if (inventories.size() == getInventory().getSize()) {
            throw new IllegalArgumentException("Navbar already at maximum item capacity.");
        }

        inventories.put(Objects.requireNonNull(representationStack), Objects.requireNonNull(inventory));
        super.clear();
        Iterator<Map.Entry<ItemStack, ASigilInventory>> it = inventories.entrySet().iterator();
        for (int i = 0; i < inventories.size() && it.hasNext(); i++) {
            Map.Entry<ItemStack, ASigilInventory> en = it.next();
            getInventory().setItem(i, en.getKey());
            final ASigilInventory inv = en.getValue();
            addListener(i, (ev) -> inv.presentTo(ev.getWhoClicked()));
        }
    }

    @Override
    public void onPlayerInteractEvent(InventoryClickEvent ev) {
        ev.setCancelled(true);
        if (ev.getClickedInventory() != null && ev.getClickedInventory().equals(getInventory())) {
            ItemStack clickedStack = getInventory().getItem(ev.getSlot());
            ASigilInventory inv = inventories.getOrDefault(clickedStack, this);
            inv.presentTo(ev.getWhoClicked());
        }
    }
}
