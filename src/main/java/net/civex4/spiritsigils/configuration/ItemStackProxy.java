package net.civex4.spiritsigils.configuration;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemStackProxy {
    private Material material;
    private int amount;
    private String displayName;
    private List<String> lore;
    private Map<String, Integer> enchantments;
    private List<String> itemFlags;
    private ItemStack COMPARE_STACK;

    public ItemStackProxy() {
    }

    public ItemStackProxy(Material material, int amount, String customName, List<String> lore, Map<String, Integer> enchantments, List<String> itemFlags) {
        this.material = material;
        this.amount = amount;
        this.displayName = customName;
        this.lore = lore;
        this.enchantments = enchantments;
        this.itemFlags = itemFlags;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public void setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public ItemStack getCompareStack() {
        if (COMPARE_STACK == null) {
            COMPARE_STACK = toItemStack();
        }

        return COMPARE_STACK;
    }

    public boolean matches(ItemStack stack) {
        return stack.isSimilar(getCompareStack()) && stack.getAmount() >= amount;
    }

    private boolean isSameEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> other = stack.getItemMeta().getEnchants();
        if (enchantments == null && other == null) {
            Bukkit.broadcast(new TextComponent("1"));
            return true;
        } else if ((enchantments == null && other != null) || (enchantments != null && other == null)) {
            Bukkit.broadcast(new TextComponent("2"));
            return false;
        } else if (enchantments.size() != other.size()) {
            Bukkit.broadcast(new TextComponent("3"));
            return false;
        } else {
            for (Map.Entry<String, Integer> en : enchantments.entrySet()) {
                if (other.get(Enchantment.getByName(en.getKey())) != en.getValue()) {
                    Bukkit.broadcast(new TextComponent("4 " + other.get(Enchantment.getByName(en.getKey())) + " " + en.getValue()));
                    return false;
                }
            }

            return true;
        }
    }

    public ItemStack toItemStack() {
        return toItemStack(getAmount());
    }

    public ItemStack toItemStack(int stacksize) {
        ItemStack stack = new ItemStack(material, stacksize);

        if (displayName != null) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
        }

        if (lore != null && !lore.isEmpty()) {
            ItemMeta meta = stack.getItemMeta();
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        if (enchantments != null && !enchantments.isEmpty()) {
            ItemMeta meta = stack.getItemMeta();
            enchantments.entrySet().forEach(en -> {
                meta.addEnchant(Enchantment.getByName(en.getKey()), en.getValue(), true);
            });
            stack.setItemMeta(meta);
        }
        if (itemFlags != null && !itemFlags.isEmpty()) {
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(itemFlags.stream().map(ItemFlag::valueOf).toArray(ItemFlag[]::new));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public boolean isSimilar(ItemStack stack) {
        return toItemStack().isSimilar(stack);
    }
}
