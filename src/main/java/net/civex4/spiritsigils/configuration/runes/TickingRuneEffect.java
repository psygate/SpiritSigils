package net.civex4.spiritsigils.configuration.runes;

import org.bukkit.entity.Player;

import java.util.List;

public interface TickingRuneEffect {
    void apply(int amountOfRunes, List<Player> players);
}
