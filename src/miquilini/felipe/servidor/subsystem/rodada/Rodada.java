package miquilini.felipe.servidor.subsystem.rodada;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;







import miquilini.felipe.intermediario.carta.Carta;
import miquilini.felipe.servidor.subsystem.mesa.Jogador;
import miquilini.felipe.servidor.subsystem.mesa.Mesa;

public class Rodada {
	public static final int vitoria = 2;	
	private float timeAPontos;
	private float timeBPontos;
	
	private final Mesa mesa;
	private List<Carta> cartas = new ArrayList<>();
	private List<Jogador> jogadores = new ArrayList<>();
	private Jogador primeiroNaQueda;
	//private boolean isPrimeira;
	
	public Rodada(Mesa mesa, Jogador primeiroNaQueda) {
		this(mesa, 0f, 0f, primeiroNaQueda);
	}
	
	public Rodada(Mesa mesa, float timeAPontos, float timeBPontos, Jogador primeiroNaQueda) {
		super();
		this.mesa = mesa;
		this.timeAPontos = timeAPontos;
		this.timeBPontos = timeBPontos;
		this.primeiroNaQueda = primeiroNaQueda;
		//mesa.resetJaTrucouGeral();
		//this.isPrimeira = primeiraVez;
	}
	
	
	public final void update(Carta carta, Jogador jogador) throws IncobertoRodadaInicialException {
		if(!podeEncobrir() && carta.equals(Carta.INCOBERTO))
			throw new IncobertoRodadaInicialException();
		
		cartas.add(carta);
		jogadores.add(jogador);
		
		if(cartas.size() != mesa.getQtdPlayers()) 
			return;
		boolean[] status = conclusoesIniciais();
		queda(status[0], status[1]);
		mostrarJogadoresECartas();
		//cartas.clear();
	}
	
	private boolean[] conclusoesIniciais() {
		Set<Carta> maisForte = Carta.pegarMaisFortes(cartas);
		boolean timeAVenceu = false, timeBVenceu = false;
		{
			boolean primeiroQueLancouAMaisForte = true;
			for(int i = 0; i < cartas.size(); i++) {
				for(Carta c : maisForte) {
					if(c.equals(cartas.get(i))) {
						//jogador que tem a carta
						Jogador j = jogadores.get(i);
						if(mesa.pertenceTimeA(j)) {
							timeAVenceu = true;
						} else {
							timeBVenceu = true;
						}
						if(primeiroQueLancouAMaisForte) {
							mesa.passaAVezPara(j.getCodigo());
							primeiroQueLancouAMaisForte = false;
						}
						/*
						if(i % 2 == 0) {
							timeAVenceu = true;
						} else {
							timeBVenceu = true;
						}*/
					}
				}
			}
			assert !primeiroQueLancouAMaisForte;
		}
		return new boolean[] {timeAVenceu, timeBVenceu};
	}
	
	public void queda(final boolean timeAVenceu, final boolean timeBVenceu) {
		boolean atingiuPontuacaoMaxima = false;
		//mostrarJogadoresECartas();
		if(timeAVenceu && timeBVenceu) {
			if(!isDesempate()) {
				timeAPontos += valorDaRodada();
				timeBPontos += valorDaRodada();
				if(timeAPontos > timeBPontos) {
					mesa.timeAVence();
				} else {
					mesa.timeBVence();
				}
				assert timeAPontos != timeBPontos;
				atingiuPontuacaoMaxima = true;
			}
		} else {
			if(timeAVenceu) {
				timeAPontos += valorDaRodada();
				if(timeAPontos >= vitoria) {
					mesa.timeAVence();
					atingiuPontuacaoMaxima = true;
				}
			}
			if(timeBVenceu) {
				timeBPontos += valorDaRodada();
				if(timeBPontos >= vitoria) {
					mesa.timeBVence();
					atingiuPontuacaoMaxima = true;
				} 
			}
		}
		if(timeAVenceu && timeBVenceu && isDesempate()) {
			desempate();
		} else if(atingiuPontuacaoMaxima) {
			mesa.novaRodada(new RodadaInicial(mesa, primeiroNaQueda.getProxJogador()));
			//System.out.println("comecou outra !!");
		} else {
			assert timeAVenceu ^ timeBVenceu : "Os dois times não poderiam ter vencido justos se o programa chegou aqui";
			//mostrarJogadoresECartas();
			if(timeAVenceu) {
				mesa.addLog("\nTime A venceu a rodada anterior.");
			} else {
				mesa.addLog("\nTime B venceu a rodada anterior.");
			}
			mesa.novaRodada(new Rodada(mesa, timeAPontos, timeBPontos, primeiroNaQueda));
		}
	}
	public boolean podeEncobrir() {
		return true;
	}
	public boolean isDesempate() {
		return false;
	}
	public void desempate() {
		//criará uma nova rodada e setará mesa.novaRodada
		//igual a esta nova rodada desempate recêm criada
		mesa.novaRodada(new RodadaDesempate(mesa, primeiroNaQueda, true));
	}
	public float valorDaRodada() {
		return 1f;
	}
	
	protected Mesa getMesa() {
		return mesa;
	}
	
	public Map<String, Carta> getCartasJogadasNaRodada() {
		Map<String, Carta> jogadoresCartas = new LinkedHashMap<>();
		{
			for(int i = 0; i < cartas.size(); i++) {
				//String s = jogadores.get(i).getNome() + " jogou " + cartas.get(i);
				//jogadoresCartas.add(s);
				jogadoresCartas.put(jogadores.get(i).getNome(), cartas.get(i));
			}
		}
		return jogadoresCartas;
	}
	
	private void mostrarJogadoresECartas() {
		for(int i = 0; i < jogadores.size(); i++) {
			mesa.addLog(new StringBuilder().append("\n   ").append(jogadores.get(i).getNome()).append(" jogou ").append(cartas.get(i).toString()).toString());
		}
	}
	
	public Jogador getPrimeiroNaQueda() {
		return primeiroNaQueda;
	}
	
	@Override
	public String toString() {
		return "Rodada Comum";
	}
	
}
