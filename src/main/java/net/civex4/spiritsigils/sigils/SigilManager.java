package net.civex4.spiritsigils.sigils;

import net.civex4.spiritsigils.SpiritSigils;
import net.civex4.spiritsigils.configuration.Configuration;
import net.civex4.spiritsigils.configuration.sigils.ResolvedSigilSetting;
import net.civex4.spiritsigils.sigils.inventory.ASigilInventory;
import net.civex4.spiritsigils.util.BlockKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SigilManager implements Listener {
    private Configuration configuration;

    public SigilManager(Configuration configuration) {
        this.configuration = configuration;
        Bukkit.getScheduler().runTaskTimer(
                SpiritSigils.getInstance(),
                () -> tickSigils(),
                configuration.getGeneralSettings().getSigilTickDelay(),
                configuration.getGeneralSettings().getSigilTickDelay()
        );
    }

    @EventHandler
    public void onBlockPlacement(BlockPlaceEvent ev) {
        Optional<ResolvedSigilSetting> opt = getSettingForItem(ev.getItemInHand());
        opt.ifPresent(setting -> {
            ev.getPlayer().getInventory().setItemInMainHand(null);

            //TODO: Add target placement event here.
            if (opt.get().getBlockType() != ev.getBlockPlaced().getType()) {
                ev.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(
                        SpiritSigils.getInstance(),
                        () -> {
                            ev.getPlayer().sendMessage("Creating sigil.");
                            ev.getBlockPlaced().setType(opt.get().getBlockType());
                            try {
                                Sigil sigil = createSigil(new BlockKey(ev.getBlock()), setting);
                                ev.getPlayer().sendMessage(ChatColor.GREEN + "Created " + setting.getSigilName() + "sigil.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        1
                );
            } else {
                throw new IllegalStateException();
            }
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent ev) {
        getSigilOpt(new BlockKey(ev.getBlock())).ifPresent(sigil -> {
            ev.setCancelled(true);
            sigil.destroy();
            removeSigil(sigil);
        });
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent ev) {
        removeAttunedPlayer(ev.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK && sigils.containsKey(new BlockKey(ev.getClickedBlock()))) {
            ev.setCancelled(true);
            sigils.get(new BlockKey(ev.getClickedBlock())).getSigilInventoryManager().getNavigationInventory().presentTo(ev.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInventoryInteract(InventoryClickEvent ev) {
        if (ev.getView().getTopInventory() != null && ev.getView().getTopInventory().getHolder() instanceof ASigilInventory) {
            ((ASigilInventory) ev.getInventory().getHolder()).onPlayerInteractEvent(ev);
        }
    }

    @EventHandler
    public void onPlayerInventoryDragEvent(InventoryDragEvent ev) {
        if (ev.getView().getTopInventory().getHolder() instanceof ASigilInventory) {
            if (ev.getNewItems().keySet().stream().anyMatch(idx -> idx < ev.getView().getTopInventory().getSize())) {
                ev.setCancelled(true);
                ev.getWhoClicked().sendMessage(ChatColor.RED + "Dragging currently disabled.");
            }
        }
    }

    private Sigil createSigil(BlockKey key, ResolvedSigilSetting setting) {
        Sigil sigil = addSigil(key, setting);
        return sigil;
    }

    public Optional<ResolvedSigilSetting> getSettingForItem(ItemStack stack) {
        for (ResolvedSigilSetting sigilSetting : configuration.getResolvedSigilSettings()) {
            if (sigilSetting.getPlacementItem().equals(stack)) {
                return Optional.of(sigilSetting);
            }
        }

        return Optional.empty();
    }

    private Map<BlockKey, Sigil> sigils = new HashMap<>();

    public Optional<Sigil> getSigilOpt(BlockKey location) {
        if (sigils.containsKey(location)) {
            return Optional.of(sigils.get(location));
        } else {
            return Optional.empty();
        }
    }

    public Sigil addSigil(BlockKey location, ResolvedSigilSetting sigilSetting) {
        if (getSigilOpt(location).isPresent()) {
            throw new IllegalStateException("Sigil duplicate @" + location);
        } else {
            Sigil sigil = new Sigil(this, location, sigilSetting);
            sigils.put(sigil.getLocation(), sigil);
            return sigil;
        }
    }

    protected void removeSigil(Sigil sigil) {
        if (sigil.getDropOnDestroy() != null) {
            sigil.destroy();
        }

        sigil.getAttunedPlayers().forEach(this::unattunePlayer);
        sigils.remove(sigil.getLocation());
    }

    private Map<UUID, Sigil> playerAttunements = new HashMap<>();

    public void attunePlayerToSigil(UUID player, Sigil sigil) {
        unattunePlayer(player);
        addAttunedPlayer(player, sigil);
        sigil.addAttunedPlayer(player);
    }

    public void unattunePlayer(UUID player) {
        if (playerAttunements.containsKey(player)) {
            Sigil sigil = playerAttunements.get(player);
            sigil.removeAttunedPlayer(player);
            removeAttunedPlayer(player);
        }
    }

    private void addAttunedPlayer(UUID player, Sigil sigil) {
        if (playerAttunements.containsKey(player)) {
            throw new IllegalStateException("Player already attuned.");
        } else {
            playerAttunements.put(player, sigil);
        }
    }

    private void removeAttunedPlayer(UUID player) {
        playerAttunements.remove(player);
    }

    public void tickSigils() {
        for (Sigil sigil : sigils.values()) {
            try {
                sigil.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlayerAttuned(UUID uniqueId) {
        return playerAttunements.containsKey(uniqueId);
    }
}
