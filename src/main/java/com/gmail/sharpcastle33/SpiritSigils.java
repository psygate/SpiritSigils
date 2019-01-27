package com.gmail.sharpcastle33;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.sharpcastle33.listeners.SigilAttunementListener;
import com.gmail.sharpcastle33.listeners.SigilCreationListener;
import com.gmail.sharpcastle33.listeners.SigilDestructionListener;
import com.gmail.sharpcastle33.listeners.SigilEventListener;
import com.gmail.sharpcastle33.listeners.SigilGUIListener;

public class SpiritSigils extends JavaPlugin {

    // Don't do this. Public static mutable fields aren't great. Use a static getter.
//	public static Plugin plugin;

    private static SpiritSigils instance = null;

    public void onEnable() {
        instance = this;
        Listener[] listeners = new Listener[]{
                new SigilCreationListener(),
                new SigilDestructionListener(),
                new SigilAttunementListener(),
                new SigilEventListener(),
                new SigilGUIListener()
        };

        for (Listener l : listeners) {
            getServer().getPluginManager().registerEvents(l, getInstance());
        }
    }

    /**
     * @return SpiritSigils instance
     * @throws IllegalStateException If the plugin hasn't been initialized yet, this exception is thrown to indicate an error.
     */
    public static SpiritSigils getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException(SpiritSigils.class.getSimpleName() + " plugin hasn't been setup properly at this time.");
        }

        return instance;
    }

    public void onDisable() {
        // Make sure other plugins don't invoke anything on this one to avoid strange side effects, after this one has been disabled.
        instance = null;
    }
}
