package miquilini.felipe.intermediario.carta;

import java.util.ArrayList;
import java.util.List;

public class UtilitarioBaralho {
	private UtilitarioBaralho() {}
	
	/**
	 * @throws IllegalArgumentException
	 * @param qtd
	 * @return
	 */
	public static Carta[] retirarCartas(final int qtd) {
		
		if(qtd < 0 || qtd > Carta.values().length - 1)
			throw new IllegalArgumentException("Quantidade inválida");
		
		
		Carta[] cartas = new Carta[qtd];
		
		List<Carta> cartasRetiraveis = new ArrayList<>();
		
		for(Carta c : Carta.values()) {
			cartasRetiraveis.add(c);
		}
		
		cartasRetiraveis.remove(Carta.INCOBERTO);
		
		for(int i = 0; i < qtd; i++) {
			int aleatorio = (int) Math.round(Math.random() * (cartasRetiraveis.size() - 1));
			
			cartas[i] = cartasRetiraveis.get(aleatorio);
			cartasRetiraveis.remove(aleatorio);
		}
		
		return cartas;
	}
}
