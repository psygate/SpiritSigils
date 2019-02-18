package net.civex4.spiritsigils.sigils;

import net.civex4.spiritsigils.configuration.runes.TickingRuneEffect;
import net.civex4.spiritsigils.configuration.sigils.ResolvedSigilSetting;
import net.civex4.spiritsigils.sigils.inventory.SigilInventoryManager;
import net.civex4.spiritsigils.util.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Sigil {
    private SigilManager sigilManager;
    private final BlockKey location;
    private Set<UUID> attunedPlayers = new HashSet<>();
    private ItemStack dropOnDestroy;
    private SigilInventoryManager sigilInventoryManager;
    private ResolvedSigilSetting sigilSetting;
    private int fuelLevel = 0;

    public Sigil(SigilManager parent, BlockKey location, ResolvedSigilSetting sigilSetting) {
        this.location = location;
        this.dropOnDestroy = sigilSetting.getPlacementItem();
        this.sigilManager = parent;
        this.sigilSetting = sigilSetting;
        this.sigilInventoryManager = new SigilInventoryManager(this);
    }

    public SigilManager getSigilManager() {
        return sigilManager;
    }

    public void setSigilManager(SigilManager sigilManager) {
        this.sigilManager = sigilManager;
    }

    public BlockKey getLocation() {
        return location;
    }

    public void destroy() {
        location.getWorld().dropItemNaturally(location.toLocation(), getDropOnDestroy());
        dropOnDestroy = null;
        location.toBlock().setType(Material.AIR);
        delete();
    }


    public void setSigilInventoryManager(SigilInventoryManager sigilInventoryManager) {
        this.sigilInventoryManager = sigilInventoryManager;
    }

    public void delete() {
        sigilManager.removeSigil(this);
    }

    public Set<UUID> getAttunedPlayers() {
        return attunedPlayers;
    }

    public void setAttunedPlayers(Set<UUID> attunedPlayers) {
        this.attunedPlayers = attunedPlayers;
    }

    public ItemStack getDropOnDestroy() {
        return dropOnDestroy;
    }

    public void setDropOnDestroy(ItemStack dropOnDestroy) {
        this.dropOnDestroy = dropOnDestroy;
    }

    public void addAttunedPlayer(UUID uuid) {
        getAttunedPlayers().add(uuid);
    }

    public void removeAttunedPlayer(UUID uuid) {
        getAttunedPlayers().remove(uuid);
    }

    public void tick() {
        if (fuelLevel <= 0 && consumeFuelItem()) {
            fuelLevel = getSigilSetting().getTicksPerFuelUnit();
        }

        if (fuelLevel > 0) {
            fuelLevel--;
            getSigilInventoryManager().getFuelInventory().setFuelLevel((float) fuelLevel / (float) getSigilSetting().getTicksPerFuelUnit());
            applyTickEffects();
        }
    }

    private void applyTickEffects() {
        // TODO
        // This may be buffered.
        Map<ItemStack, Integer> runeItems = getSigilInventoryManager().getRuneInventory().collectRunes();
        List<Player> players = getAttunedPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(x -> x != null)
                .collect(Collectors.toList());

        getSigilSetting().getRuneSettings().stream()
                .filter(e -> e instanceof TickingRuneEffect)
                .filter(e -> runeItems.containsKey(e.getItem().toItemStack(1)))
                .forEach(t -> t.apply(runeItems.get(t.getItem().toItemStack(1)), players));
    }

    private boolean consumeFuelItem() {
        return getSigilInventoryManager().getFuelInventory().consumeFuelItem();
    }

    public SigilInventoryManager getSigilInventoryManager() {
        return sigilInventoryManager;
    }

    public ResolvedSigilSetting getSigilSetting() {
        return sigilSetting;
    }

    public void setSigilSetting(ResolvedSigilSetting sigilSetting) {
        this.sigilSetting = sigilSetting;
    }
}
