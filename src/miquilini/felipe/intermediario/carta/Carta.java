package miquilini.felipe.intermediario.carta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Carta {
	PAUS_4(0, "Quatro de Paus", 1), COPAS_4(1, "Quatro de Copas", 1), ESPADAS_4(2, "Quatro de Espadas", 1), OUROS_4(3, "Quatro de Ouros", 1),
	PAUS_5(4, "Cinco de Paus", 2), COPAS_5(5, "Cinco de Copas", 2), ESPADAS_5(6, "Cinco de Espadas", 2), OUROS_5(7, "Cinco de Ouros", 2),
	PAUS_6(8, "Seis de Paus", 3), COPAS_6(9, "Seis de Copas", 3), ESPADAS_6(10, "Seis de Espadas", 3), OUROS_6(11, "Seis de Ouros", 3),
	PAUS_7(12, "Sete de Paus", 4), COPAS_7(13, "Sete de Copas", 4), ESPADAS_7(14, "Sete de Espadas", 4), OUROS_7(15, "Sete de Ouros", 4),
	PAUS_Q(16, "Dama de Paus", 5), COPAS_Q(17, "Dama de Copas", 5), ESPADAS_Q(18, "Dama de Espadas", 5), OUROS_Q(19, "Dama de Ouros", 5),
	PAUS_J(20, "Valete de Paus", 6), COPAS_J(21, "Valete de Copas", 6), ESPADAS_J(22, "Valete de Espadas", 6), OUROS_J(23, "Valete de Ouros", 6),
	PAUS_K(24, "Rei de Paus", 7), COPAS_K(25, "Rei de Copas", 7), ESPADAS_K(26, "Rei de Espadas", 7), OUROS_K(27, "Rei de Ouros", 7),
	PAUS_A(28, "Ás de Paus", 8), COPAS_A(29, "Ás de Copas", 8), ESPADAS_A(30, "Ás de Espadas", 8), OUROS_A(31, "Ás de Ouros", 8),
	PAUS_2(32, "Dois de Paus", 9), COPAS_2(33, "Dois de Copas", 9), ESPADAS_2(34, "Dois de Espadas", 9), OUROS_2(35, "Dois de Ouros", 9),
	PAUS_3(36, "Três de Paus", 10), COPAS_3(37, "Três de Copas", 10), ESPADAS_3(38, "Três de Espadas", 10), OUROS_3(39, "Três de Ouros", 10),
	INCOBERTO(40, "Incoberto", 0);
	
	private final int codigo;
	private int forcaBruta;
	private int adicionalManilha;
	private String nome;
	
	
	Carta(int codigo, String nome, int forcaBruta) {
		this.codigo = codigo;
		this.nome = nome;
		this.forcaBruta = forcaBruta;
		adicionalManilha = 0;
	}
	
	public static void gerarManilha(Carta vira) {
		//para zerar a manilha da rodada anterior
		Carta[] todasCartas = Carta.values();
		for(Carta carta : todasCartas) {
			carta.adicionalManilha = 0;
		}
		
		int codPausDaManilha = 0;
		
		if(vira.codigo < 36) 
			codPausDaManilha = vira.codigo + 4 - (vira.codigo % 4);	
		
		for(int i = 0, j = 13; i < 4; i++, j--) { 
			todasCartas[codPausDaManilha + i].adicionalManilha = j;
		}
		
	}
	
	public int getForca() {
		return forcaBruta + adicionalManilha;
	}
	
	/**
	 * 
	 * @param cartas
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Set<Carta> pegarMaisFortes(List<Carta> cartas) throws IllegalArgumentException{
		
		if(cartas == null || cartas.size() == 0)
			throw new IllegalArgumentException();
		
		Set<Carta> maisFortes = new HashSet<>();
		
		for(Carta ca : cartas) {
			
			if(maisFortes.size() == 0) {
				maisFortes.add(ca);
				continue;
			}
			
			int forcaDaCartaMaisForte = maisFortes.iterator().next().getForca();
			int forcaDaCartaCA = ca.getForca();
			if(forcaDaCartaMaisForte == forcaDaCartaCA) {
				maisFortes.add(ca);
				continue;
			} 
			if(forcaDaCartaCA > forcaDaCartaMaisForte) {
				maisFortes.clear();
				maisFortes.add(ca);
				continue;
			}
		}
		
		assert maisFortes.size() > 0;
		
		return maisFortes;
	}
	
	public static Carta getCarta(String nome) throws IllegalArgumentException{
		Carta carta = null;
		for(Carta c : values()) {
			if(c.nome.equals(nome)) {
				carta = c;
				break;
			}
		}
		if(carta == null)
			throw new IllegalStateException("Este nome de carta não existe");
		
		return carta;
	}
	
	@Override
	public String toString() {
		return nome;
	}
	
}
