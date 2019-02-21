package net.civex4.spiritsigils.configuration.runes;

import net.civex4.spiritsigils.configuration.ItemStackProxy;
import net.civex4.spiritsigils.sigils.Sigil;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class MineDropCollectionRune extends RuneSetting implements PersistentRuneEffect {
    public MineDropCollectionRune() {
    }

    public MineDropCollectionRune(String runeName, ItemStackProxy item, int maximumRunesPerSigil) {
        super(runeName, item, maximumRunesPerSigil);
    }

    @Override
    public void apply(Sigil sigil) {
        sigil.getSigilManager().getEventManager().addListener(sigil, BlockBreakEvent.class, blockBreakEvent -> {
            if (blockBreakEvent.isDropItems()
                    && sigil.getAttunedPlayers().contains(blockBreakEvent.getPlayer().getUniqueId())
                    && sigil.isInRange(blockBreakEvent.getPlayer())
            ) {
                blockBreakEvent.setDropItems(false);
                Collection<ItemStack> items = blockBreakEvent.getBlock().getDrops(blockBreakEvent.getPlayer().getInventory().getItemInMainHand());
                ItemStack[] itemsArray = items.toArray(new ItemStack[items.size()]);
                HashMap<Integer, ItemStack> leftOvers = sigil.getSigilInventoryManager().getItemInventory().getInventory().addItem(itemsArray);
                if (!leftOvers.isEmpty()) {
                    Location loc = blockBreakEvent.getBlock().getLocation();
                    leftOvers.values().forEach(i -> loc.getWorld().dropItemNaturally(loc, i));
                }
            }
        });

    }
}
