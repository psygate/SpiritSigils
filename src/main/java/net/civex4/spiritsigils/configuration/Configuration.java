package net.civex4.spiritsigils.configuration;

import net.civex4.spiritsigils.configuration.runes.RuneSetting;
import net.civex4.spiritsigils.configuration.runes.PotionEffectRuneSetting;
import net.civex4.spiritsigils.configuration.sigils.ResolvedSigilSetting;
import net.civex4.spiritsigils.configuration.sigils.SigilSetting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Configuration {
    private int VERSION = 1;
    private GeneralSettings generalSettings;
    private List<SigilSetting> sigilSettings = new ArrayList<>();
    private List<RuneSetting> runeSettings = new ArrayList<>();

    public Configuration() {

    }

    public Configuration(int VERSION, GeneralSettings generalSettings, List<SigilSetting> sigilSettings, List<RuneSetting> runeSettings) {
        this.VERSION = VERSION;
        this.generalSettings = generalSettings;
        this.sigilSettings = sigilSettings;
        this.runeSettings = runeSettings;
    }

    public List<RuneSetting> getRuneSettings() {
        return runeSettings;
    }

    public void setRuneSettings(List<RuneSetting> runeSettings) {
        this.runeSettings = runeSettings;
    }

    public List<ResolvedSigilSetting> getResolvedSigilSettings() {
        Map<String, RuneSetting> runes = getRuneSettings()
                .stream()
                .collect(Collectors.toMap(RuneSetting::getRuneName, Function.identity()));

        for (SigilSetting setting : getSigilSettings()) {
            for (String runename : setting.getRuneLimits().keySet()) {
                if (!runes.containsKey(runename)) {
                    throw new IllegalArgumentException("No rune by name " + runename);
                }
            }
        }

        return getSigilSettings().stream()
                .map(s -> new ResolvedSigilSetting(
                        s,
                        runes.values().stream()
                                .filter(r -> s.getRuneLimits().containsKey(r.getRuneName()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public List<SigilSetting> getSigilSettings() {
        return sigilSettings;
    }

    public void setSigilSettings(List<SigilSetting> sigilSettings) {
        this.sigilSettings = sigilSettings;
    }

    public int getVERSION() {
        return VERSION;
    }

    public void setVERSION(int VERSION) {
        this.VERSION = VERSION;
    }

    public GeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    public void setGeneralSettings(GeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }

    public static Configuration getDefaultConfig() {
        return new Configuration(
                1,
                new GeneralSettings(
                        20
                ),
                Arrays.asList(
                        new SigilSetting(
                                ChatColor.GOLD + "Mining Sigil",
                                new ItemStackProxy(
                                        Material.DIAMOND_BLOCK,
                                        64,
                                        ChatColor.GOLD + "Mining Sigil",
                                        Arrays.asList(ChatColor.WHITE + "Place this to create target Mining Sigil."),
                                        new HashMap<String, Integer>() {
                                            {
                                                put(Enchantment.DURABILITY.getName(), 1);
                                            }
                                        },
                                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                                ),
                                Material.BLACK_SHULKER_BOX,
                                4,
                                8,
                                new ItemStackProxy(
                                        Material.GOLD_INGOT,
                                        1,
                                        ChatColor.DARK_RED + "Sigil Fuel",
                                        Arrays.asList(ChatColor.WHITE + "Place this in target mining sigil to fuel it."),
                                        new HashMap<String, Integer>() {
                                            {
                                                put(Enchantment.DURABILITY.getName(), 1);
                                            }
                                        },
                                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                                ),
                                5,
                                new HashMap<String, Integer>() {{
                                    put("Rune of Swiftness", 2);
                                    put("Rune of Regeneration", 2);
                                }}
                        )
                ),
                Arrays.asList(
                        new PotionEffectRuneSetting(
                                "Rune of Swiftness",
                                new ItemStackProxy(
                                        Material.DIAMOND,
                                        64,
                                        ChatColor.GOLD + "Rune of Swiftness",
                                        Arrays.asList(ChatColor.WHITE + "Place this in target sigil to enhance your movement speed.."),
                                        new HashMap<String, Integer>() {
                                            {
                                                put(Enchantment.DURABILITY.getName(), 1);
                                            }
                                        },
                                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                                ),
                                4,
                                PotionEffectType.SPEED.getName(),
                                1,
                                21
                        ),
                        new PotionEffectRuneSetting(
                                "Rune of Regeneration",
                                new ItemStackProxy(
                                        Material.DIAMOND,
                                        64,
                                        ChatColor.GOLD + "Rune of Regeneration",
                                        Arrays.asList(ChatColor.WHITE + "Place this in target sigil to grant regenration."),
                                        new HashMap<String, Integer>() {
                                            {
                                                put(Enchantment.DURABILITY.getName(), 1);
                                            }
                                        },
                                        Arrays.asList(ItemFlag.HIDE_ENCHANTS.name())
                                ),
                                4,
                                PotionEffectType.REGENERATION.getName(),
                                1,
                                21
                        )
                )
        );
    }
}
