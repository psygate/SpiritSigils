package com.gmail.sharpcastle33;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.sharpcastle33.listeners.SigilAttunementListener;
import com.gmail.sharpcastle33.listeners.SigilCreationListener;
import com.gmail.sharpcastle33.listeners.SigilDestructionListener;
import com.gmail.sharpcastle33.listeners.SigilEventListener;
import com.gmail.sharpcastle33.listeners.SigilGUIListener;

public class SpiritSigils extends JavaPlugin {
	
	/*
	 * Spirit Sigils main class.
	 */
	
	public static Plugin plugin;
	
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(new SigilCreationListener(), plugin);			// Detects and handles sigil creation
		getServer().getPluginManager().registerEvents(new SigilDestructionListener(), plugin);			// Detects and handles sigil destruction
		getServer().getPluginManager().registerEvents(new SigilAttunementListener(), plugin);			//Detects and handles sigil attunement
		getServer().getPluginManager().registerEvents(new SigilEventListener(), plugin);			// Detects and handles sigil-related events (eg mining sigil pickup)
		getServer().getPluginManager().registerEvents(new SigilGUIListener(), plugin);			// Detects and handles sigil GUI (if applicable)

		
	}
	
	public void onDisable() {
		
	}
	

}
