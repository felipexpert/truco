package miquilini.felipe.clienteSwing.som;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class MP3 {
	private String name;
	private Player player;
	
	private boolean continuar = true;
	
	public MP3(String name) {
		this.name = name;
	}
	
	public void playContinuo() {
		if(continuar) {
			play();
			playContinuo();
		} else {
			if(!continuar)
				continuar = true;
		}
	}
	
	public void stop() {
		if(continuar)
			continuar = false;
		
		player.close();
	}
	
	public void play() {
		try {
			
			player = new Player(getClass().getResourceAsStream(name));
			
			player.play();
			
		} catch (JavaLayerException e) {
			e.printStackTrace();
			assert false;
		}
	}
}
