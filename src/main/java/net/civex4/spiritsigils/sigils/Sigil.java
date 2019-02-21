package net.civex4.spiritsigils.sigils;

import net.civex4.spiritsigils.configuration.runes.PersistentRuneEffect;
import net.civex4.spiritsigils.configuration.runes.TickingRuneEffect;
import net.civex4.spiritsigils.configuration.sigils.ResolvedSigilSetting;
import net.civex4.spiritsigils.sigils.inventory.SigilInventoryManager;
import net.civex4.spiritsigils.util.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Sigil {
    private SigilManager sigilManager;
    private final BlockKey location;
    private Set<UUID> attunedPlayers = new HashSet<>();
    private ItemStack dropOnDestroy;
    private SigilInventoryManager sigilInventoryManager;
    private ResolvedSigilSetting sigilSetting;
    private int fuelLevel = 0;

    private HashMap<UUID, Long> lastWarningToPlayer = new HashMap<>();

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
        getSigilManager().getEventManager().removeSigil(this);
        getAttunedPlayers().forEach(u -> getSigilManager().unattunePlayer(u));
        getAttunedPlayers().forEach(this::removeAttunedPlayer);
    }


    public void setSigilInventoryManager(SigilInventoryManager sigilInventoryManager) {
        this.sigilInventoryManager = sigilInventoryManager;
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
            applyListenerEffects();
        }
    }

    private void applyListenerEffects() {
        getSigilManager().getEventManager().clear(this);
        Map<ItemStack, Integer> runeItems = getSigilInventoryManager().getRuneInventory().collectRunes();
        getSigilSetting().getRuneSettings().stream()
                .filter(e -> e instanceof PersistentRuneEffect)
                .filter(e -> runeItems.containsKey(e.getItem().toItemStack(1)))
                .forEach(t -> ((PersistentRuneEffect) t).apply(this));
    }

    private void applyTickEffects() {
        // TODO
        // This may be buffered.
        Map<ItemStack, Integer> runeItems = getSigilInventoryManager().getRuneInventory().collectRunes();
        List<Player> allPlayers = getAttunedPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(x -> x != null)
                .collect(Collectors.toList());

        allPlayers.stream()
                .filter(this::isOutOfRange)
                .filter(this::shouldBeWarned)
                .forEach(this::warnAboutRange);

        List<Player> players = allPlayers.stream().filter(this::isInRange).collect(Collectors.toList());

        getSigilSetting().getRuneSettings().stream()
                .filter(e -> e instanceof TickingRuneEffect)
                .filter(e -> runeItems.containsKey(e.getItem().toItemStack(1)))
                .forEach(t -> ((TickingRuneEffect) t).apply(runeItems.get(t.getItem().toItemStack(1)), players));
    }

    private void warnAboutRange(Player player) {
        player.sendMessage(ChatColor.RED + "Sigil range exceeded.");
        lastWarningToPlayer.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean shouldBeWarned(Player player) {
        return TimeUnit.MILLISECONDS.toSeconds(lastWarningToPlayer.getOrDefault(player.getUniqueId(), 0L)) >= 30;
    }

    public boolean isInRange(Player x) {
        if (!getLocation().getWorldUUID().equals(x.getWorld().getUID())) {
            return false;
        } else {
            return getLocation().distanceSqr(x.getLocation()) <= getSigilSetting().getMaxRange() * getSigilSetting().getMaxRange();
        }
    }

    public boolean isOutOfRange(Player x) {
        return !isInRange(x);
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

    public void unattunePlayerAndRemoveEffects(Player player) {
        if (attunedPlayers.contains(player.getUniqueId())) {
            //TODO add an effect cache, so the old effects get reapplied to the player.
            player.getActivePotionEffects().clear();
            getSigilInventoryManager().getAttunementInventory().unattunePlayer(player);
        }
    }
}
