package com.gmail.sharpcastle33.sigils;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.sharpcastle33.util.Constants;

public class MiningSigil extends Sigil {
	
	private ArrayList<Player> attunedPlayers;
	int maxAttuned = Constants.miningSigilAttunementLimit;
	
	public void attunePlayer(Player p) {
		attunedPlayers.add(p);
	}
	
	public void unattunePlayer(Player p) {
		attunedPlayers.remove(p);
	}

}
