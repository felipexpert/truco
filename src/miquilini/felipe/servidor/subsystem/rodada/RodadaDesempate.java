package miquilini.felipe.servidor.subsystem.rodada;

import miquilini.felipe.servidor.subsystem.mesa.Jogador;
import miquilini.felipe.servidor.subsystem.mesa.Mesa;

public class RodadaDesempate extends Rodada {
	private final boolean isPrimeira;
	public RodadaDesempate(Mesa mesa, Jogador primeiroJogador, boolean isPrimeira) {
		super(mesa, primeiroJogador);
		this.isPrimeira = isPrimeira;
	}
	@Override
	public void queda(boolean timeAVenceu, boolean timeBVenceu) {
		Mesa mesa = getMesa();
		if(timeAVenceu && timeBVenceu) {
			if(isPrimeira) {
				mesa.novaRodada(new RodadaDesempate(getMesa(), getPrimeiroNaQueda(), false));
			} else {
				//mostrarJogadoresECartas();
				mesa.ninguemVenceu();
				mesa.novaRodada(new RodadaInicial(getMesa(), getPrimeiroNaQueda().getProxJogador()));
			}
		} else if(timeAVenceu) {
			mesa.timeAVence();
			mesa.novaRodada(new RodadaInicial(getMesa(), getPrimeiroNaQueda().getProxJogador()));
			//mostrarJogadoresECartas();
			mesa.addLog("\nTime A venceu a rodada anterior.");
		} else if(timeBVenceu) {
			mesa.timeBVence();
			mesa.novaRodada(new RodadaInicial(getMesa(), getPrimeiroNaQueda().getProxJogador()));
			//mostrarJogadoresECartas();
			mesa.addLog("\nTime B venceu a rodada anterior.");
		} else {
			assert false;
		}
	}
	
	@Override
	public String toString() {
		return isPrimeira ? "Desempate" : "Segundo Desempate";
	}
}
