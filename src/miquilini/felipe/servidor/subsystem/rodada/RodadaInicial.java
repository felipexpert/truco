package miquilini.felipe.servidor.subsystem.rodada;

import miquilini.felipe.intermediario.carta.UtilitarioBaralho;
import miquilini.felipe.servidor.subsystem.mesa.Jogador;
import miquilini.felipe.servidor.subsystem.mesa.Mesa;

public class RodadaInicial extends Rodada {
	public RodadaInicial(Mesa mesa, Jogador primeiroNaQueda) {
		super(mesa, primeiroNaQueda);
		//programar para embaralhar, tirar a vira, e entregar as cartas
		mesa.resetJaTrucouGeral();
		mesa.passaAVezPara(primeiroNaQueda.getCodigo());
		mesa.novaQueda(UtilitarioBaralho.retirarCartas((mesa.getQtdPlayers() * 3) + 1));
		
		mesa.preparaMaoDe11SeCoerenteFor(primeiroNaQueda);
	}
	@Override
	public boolean isDesempate() {
		return true;
	}
	@Override
	public float valorDaRodada() {
		return 1.5f;
	}
	@Override
	public boolean podeEncobrir() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Rodada Inicial";
	}
}
