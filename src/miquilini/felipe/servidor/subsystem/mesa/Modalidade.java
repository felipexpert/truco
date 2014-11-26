package miquilini.felipe.servidor.subsystem.mesa;

public enum Modalidade {
	SIMPLES(2), DUPLA(4), DOURADINHA(6);
	
	private int qtdPlayers;
	
	Modalidade(int qtdPlayers) {
		this.qtdPlayers = qtdPlayers;
	}
	
	public int getQtdPlayers() {
		return qtdPlayers;
	}
	
	public int playerAtual(int jogadas) {
		return jogadas % qtdPlayers;
	}
}
