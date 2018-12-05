package com.gmail.sharpcastle33.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.sharpcastle33.util.Constants;

public class SigilCreationListener implements Listener{
	
	@EventHandler
	public void placeSigil(BlockPlaceEvent event) {
		
		Player p = event.getPlayer();
		Block b = event.getBlock();
		ItemStack item = event.getItemInHand();
		
		//MINING SIGIL
		if(b.getType() == Material.BLACK_SHULKER_BOX) {
			if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(Constants.MINING_SIGIL_ITEM_NAME)) {
			 	
				if(b.getWorld().getHighestBlockAt(b.getLocation()) == b) {
					
				}else {
					event.setCancelled(true);
					p.sendMessage(Constants.REQUIRES_SKY);
					return;
				}
				
			}
		}
		
	}
	
	@EventHandler
	public void placeStorage(BlockPlaceEvent event) {
		
		Player p = event.getPlayer();
		Block b = event.getBlock();
		
		if(b.getType() == Material.CHEST) {
			
		}
	}

}
