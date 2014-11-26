package miquilini.felipe.servidor.subsystem.mesa;

import java.util.Set;

public class Time {
	public static final int vitoria = 12;
	private final Set<Jogador> jogadores;
	private int pontos = 0;
	private boolean jaTrucou;
	
	public Time(Set<Jogador> jogadores) {
		this.jogadores = jogadores;
	}
	
	public boolean getJaTrucou() {
		return jaTrucou;
	}
	
	public void setJaTrucou(boolean b) {
		jaTrucou = b;
	}
	
	public boolean contains(Jogador jogador) {
		return jogadores.contains(jogador);
	}
	
	public int getPontos() {
		return pontos;
	}
	
	public void addPontos(int pontos) {
		this.pontos += pontos;
	}
	
	public boolean isVitorioso() {
		return pontos >= vitoria;
	}
	
	public Set<Jogador> getJogadores() {
		return jogadores;
	}
}
