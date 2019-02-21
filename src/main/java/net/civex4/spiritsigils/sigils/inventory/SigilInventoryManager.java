package net.civex4.spiritsigils.sigils.inventory;

import net.civex4.spiritsigils.configuration.ItemStackProxy;
import net.civex4.spiritsigils.sigils.Sigil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.HashMap;

public class SigilInventoryManager {
    private final Sigil sigil;
    private final NavigationInventory navigationInventory;
    private final FuelInventory fuelInventory;
    private final RuneInventory runeInventory;
    private final AttunementInventory attunementInventory;
    private final ItemInventory itemInventory;

    public SigilInventoryManager(Sigil sigil) {
        this.sigil = sigil;
        navigationInventory = new NavigationInventory(this);
        fuelInventory = new FuelInventory(this);
        runeInventory = new RuneInventory(this);
        attunementInventory = new AttunementInventory(this);
        itemInventory = new ItemInventory(this);

        navigationInventory.addInventory(
                new ItemStackProxy(
                        Material.FURNACE,
                        1,
                        ChatColor.DARK_PURPLE + "Fuel Menu",
                        Arrays.asList(ChatColor.WHITE + "Click here to go to the fuel menu of this sigil."),
                        new HashMap<String, Integer>() {{
                            put(Enchantment.DURABILITY.getName(), 1);
                        }},
                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                ).toItemStack(), fuelInventory
        );
        navigationInventory.addInventory(
                new ItemStackProxy(
                        Material.DIAMOND,
                        1,
                        ChatColor.DARK_PURPLE + "Rune Menu",
                        Arrays.asList(ChatColor.WHITE + "Click here to go to the rune menu of this sigil."),
                        new HashMap<String, Integer>() {{
                            put(Enchantment.DURABILITY.getName(), 1);
                        }},
                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                ).toItemStack(), runeInventory
        );
        navigationInventory.addInventory(
                new ItemStackProxy(
                        Material.ENDER_PEARL,
                        1,
                        ChatColor.DARK_PURPLE + "Attunement Menu",
                        Arrays.asList(ChatColor.WHITE + "Click here to go to the attunement menu of this sigil."),
                        new HashMap<String, Integer>() {{
                            put(Enchantment.DURABILITY.getName(), 1);
                        }},
                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                ).toItemStack(), attunementInventory
        );

        navigationInventory.addInventory(
                new ItemStackProxy(
                        Material.CHEST,
                        1,
                        ChatColor.DARK_PURPLE + "Item Inventory",
                        Arrays.asList(ChatColor.WHITE + "Click here to go to the item inventory of this sigil."),
                        new HashMap<String, Integer>() {{
                            put(Enchantment.DURABILITY.getName(), 1);
                        }},
                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                ).toItemStack(), itemInventory
        );
    }

    public Sigil getSigil() {
        return sigil;
    }

    public NavigationInventory getNavigationInventory() {
        return navigationInventory;
    }

    public FuelInventory getFuelInventory() {
        return fuelInventory;
    }

    public RuneInventory getRuneInventory() {
        return runeInventory;
    }

    public AttunementInventory getAttunementInventory() {
        return attunementInventory;
    }

    public ItemInventory getItemInventory() {
        return itemInventory;
    }
}
