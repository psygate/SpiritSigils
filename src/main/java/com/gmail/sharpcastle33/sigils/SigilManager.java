package com.gmail.sharpcastle33.sigils;

import java.util.ArrayList;

public class SigilManager {
	
	ArrayList<Sigil> activeSigils;
	
	public SigilManager() {
		activeSigils = new ArrayList<Sigil>();
	}
	
	public void addSigil(Sigil s) {
		activeSigils.add(s);
	}
	
	public void removeSigil(Sigil s) {
		activeSigils.remove(s);
	}

}
