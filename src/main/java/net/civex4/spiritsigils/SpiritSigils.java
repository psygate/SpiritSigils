package net.civex4.spiritsigils;

import net.civex4.spiritsigils.configuration.Configuration;
import net.civex4.spiritsigils.configuration.ItemStackProxy;
import net.civex4.spiritsigils.sigils.SigilManager;
import net.civex4.spiritsigils.util.BlockKey;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SpiritSigils extends JavaPlugin {

    // Don't do this. Public static mutable fields aren't great. Use target static getter.
//	public static Plugin plugin;
    private static final boolean DEBUG;
    private static SpiritSigils instance = null;

    static {
        DEBUG = Files.exists(Paths.get(".debug_plugins"));
    }

    private Configuration configuration;
    private SigilManager sigilManager;

    public static boolean DEBUG() {
        return DEBUG;
    }

    @Override
    public void onEnable() {
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            onEnableThrowing();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnableThrowing() throws Exception {
        instance = this;
        configuration = loadConfiguration();
        sigilManager = new SigilManager(configuration);

        getServer().getPluginManager().registerEvents(sigilManager, this);
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInteract(PlayerInteractEvent ev) {
                if (ev.getItem() != null && ev.getItem().getType() == Material.STICK && ev.getClickedBlock() != null) {
                    sigilManager.getSigilOpt(new BlockKey(ev.getClickedBlock())).ifPresent(sigil -> {
                        sigilManager.attunePlayerToSigil(ev.getPlayer().getUniqueId(), sigil);
                        ev.getPlayer().sendMessage("Attuned to sigil.");
                        ev.setCancelled(true);
                    });
                }
            }
        }, this);

        if (DEBUG()) {
            Bukkit.broadcast(new TextComponent(ChatColor.RED + "Sigil DEBUG MODE ENABLED."));
            for (Player p : getServer().getOnlinePlayers()) {
                p.getInventory().clear();
                configuration.getSigilSettings().forEach(s -> {
                    p.getInventory().addItem(s.getPlacementItem().toItemStack());
                });
                p.getInventory().addItem(new ItemStack(Material.STICK, 1));
                configuration.getRuneSettings().forEach(e -> {
                    p.getInventory().addItem(e.getItem().toItemStack());
                });
                configuration.getSigilSettings().forEach(s -> {
                    ItemStack e = s.getFuelItem().toItemStack();
                    e.setAmount(64);
                    p.getInventory().addItem(e);
                });
            }
        }
    }

    public static Configuration getConfiguration() {
        return getInstance().configuration;
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

    private Configuration loadConfiguration() throws IOException {
        Path configPath = Paths.get(getDataFolder().getPath(), "config.yml");
        if (DEBUG()) {
            getLogger().info(".debug_plugins file detected, deleting configuration.");
            Files.deleteIfExists(configPath);
            try (FileWriter out = new FileWriter(configPath.toFile())) {
                DumperOptions opt = new DumperOptions();
                opt.setPrettyFlow(true);
                opt.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(opt);
                yaml.dump(Configuration.getDefaultConfig(), out);
            }
        } else {
            saveDefaultConfig();
        }

        Yaml yaml = new Yaml();

        try (BufferedReader in = new BufferedReader(new FileReader(configPath.toFile()))) {
            Configuration conf = yaml.loadAs(in, Configuration.class);
            return conf;
        }
    }
}
