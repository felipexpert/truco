package miquilini.felipe.servidor.subsystem.mesa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import miquilini.felipe.intermediario.carta.Carta;
import miquilini.felipe.servidor.subsystem.mesa.exception.EmPropDeTrucoException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPodeTrucarNaPrimeiraRodadaException;
import miquilini.felipe.servidor.subsystem.rodada.IncobertoRodadaInicialException;
import miquilini.felipe.servidor.subsystem.rodada.Rodada;
import miquilini.felipe.servidor.subsystem.rodada.RodadaInicial;

public class Mesa {
	private final Modalidade modalidade;
	private Rodada rodadaAtual;
	private Time timeA, timeB;
	private boolean emPropDeTruco = false;
	private int pontos = 0;
	private Carta vira;
	
	private StringBuilder log = new StringBuilder();
	
	public Mesa(Modalidade modalidade) {
		this.modalidade = modalidade;		
	}
	
	public void comecar() throws IllegalStateException{
		if(timeA == null || timeB == null)
			throw new IllegalStateException("Os times ainda não foram definidos");
		Jogador inicial = null;
		for(Jogador j : timeA.getJogadores()) {
			if(j.getCodigo() == 0) {
				inicial = j;
				break;
			}
		}
		rodadaAtual = new RodadaInicial(this, inicial);
		log.append("Início da partida");
	}
	
	public int getQtdPlayers() {
		return modalidade.getQtdPlayers();
	}
	
	public void jogar(Carta carta, Jogador jogador) throws IncobertoRodadaInicialException, EmPropDeTrucoException {
		if(emPropDeTruco)
			throw new EmPropDeTrucoException();
		rodadaAtual.update(carta, jogador);
	}
	
	public void timeAVence() {
		if(pontos == 0)
			pontos++;
		timeA.addPontos(pontos);
		log.append("\nTime A vence, faturando ").append(pontos).append(" ponto(s).");
		pontos = 0;
	}
	
	public void ninguemVenceu() {
		log.append("\nNão houve vencedores.");
		pontos = 0;
	}
	
	public void timeBVence() {
		if(pontos == 0)
			pontos++;
		timeB.addPontos(pontos);
		log.append("\nTime B vence, faturando ").append(pontos).append(" ponto(s).");
		pontos = 0;	
	}
	
	public void novaRodada(Rodada rodada) {
		rodadaAtual = rodada;
	}
	
	public void fugir(Jogador rejeitador) {
		if(timeA.contains(rejeitador)) {
			timeBVence();
		} else {
			timeAVence();
		}
		emPropDeTruco = false;
		rodadaAtual = new RodadaInicial(this, rodadaAtual.getPrimeiroNaQueda().getProxJogador());
	}
	
	public void aceitarTruco() {
		pontos += 3;
		emPropDeTruco = false;
	}
	
	public int getValendo() {
		if(pontos == 0)
			return 1;
		else
			return pontos;
	}
	
	public void proporMaoDe11() {
		emPropDeTruco = true;
	}
	
	public void preparaMaoDe11SeCoerenteFor(Jogador primeiroNaQueda) {
		if(!isMaoDe11())
			return;
		
		if(timeA.contains(primeiroNaQueda)) {
			if(timeA.getPontos() == 11) {
				primeiroNaQueda.receberPropMaoDe11();
			} else {
				primeiroNaQueda.getProxJogador().receberPropMaoDe11();
			}
		} else if(timeB.contains(primeiroNaQueda)){
			if(timeB.getPontos() == 11) {
				primeiroNaQueda.receberPropMaoDe11();
			} else {
				primeiroNaQueda.getProxJogador().receberPropMaoDe11();
			}
		} else {
			assert false;
		}
	}
	
	public void proporTruco() throws NaoPodeTrucarNaPrimeiraRodadaException {
		if(rodadaAtual instanceof RodadaInicial)
			throw new NaoPodeTrucarNaPrimeiraRodadaException();
		emPropDeTruco = true;
	}
	
	public void novaQueda(Carta[] cartas) {
		if(cartas.length != (getQtdPlayers() * 3) + 1)
			throw new IllegalArgumentException("Número de cartas incoerente");
		
		//System.out.println("nova queda(mesa) tem " + cartas.length + " cartas");
		Carta.gerarManilha(cartas[0]);
		vira = cartas[0];
		//count = 1 porque o index 0 é a vira
		{
			int count = 1;
			for(Jogador j : getJogadores()) {
				List<Carta> mao = new ArrayList<>();;
				for(int x = 0; x < 3; x++) {
					mao.add(cartas[count++]);
				}
				j.setMao(mao);
			}
		}
	}
	
	private Set<Jogador> getJogadores() {
		Set<Jogador> set = new HashSet<>();
		set.addAll(timeA.getJogadores());
		set.addAll(timeB.getJogadores());
		
		return set;
	}
	
	public void setTimeA(Time time) {
		timeA = time; 
	}
	
	public void setTimeB(Time time) {
		timeB = time;
	}
	
	public boolean pertenceTimeA(Jogador jogador) {
		return timeA.contains(jogador);
	}
	
	public boolean pertenceTimeB(Jogador jogador) {
		return timeB.contains(jogador);
	}
	
	public void passaAVezPara(int codigo) {
		Set<Jogador> jogadores = new HashSet<>();
		jogadores.addAll(timeA.getJogadores());
		jogadores.addAll(timeB.getJogadores());
		for(Jogador j : jogadores) {
			if(j.getCodigo() == codigo)
				j.setTemAVez(true);
			else
				j.setTemAVez(false);
		}
	}
	
	public Map<String, Carta> getCartasJogadasNaRodadaAtual() {
		return rodadaAtual.getCartasJogadasNaRodada();
	}
	
	public Time getTimeA() {
		return timeA;
	}
	
	public Time getTimeB() {
		return timeB;
	}
	
	public TreeSet<Jogador> getJogadoresNaOrdem() {
		TreeSet<Jogador> ts = new TreeSet<>();
		for(Jogador j : timeA.getJogadores()) {
			ts.add(j);
		}
		for(Jogador j : timeB.getJogadores()) {
			ts.add(j);
		}
		return ts;
	}
	public Rodada getRodadaAtual() {
		return rodadaAtual;
	}
	
	public String getLog() {
		return log.toString();
	}
	
	public Carta getVira() {
		return vira;
	}
	
	public void addLog(String texto) {
		log.append(texto);
	}
	public Jogador getJogadorAtual() {
		Set<Jogador> jogadores = new HashSet<>();
		jogadores.addAll(timeA.getJogadores());
		jogadores.addAll(timeB.getJogadores());
		Jogador atual = null;
		for(Jogador j : jogadores) {
			if(j.temAVez()) {
				atual = j;
				break;
			}
		}
		
		assert atual != null;
		return atual;
	}
	
	public Time getTime(Jogador jogador) {
		Time t = null;
		
		if(timeA.contains(jogador))
			t = timeA;
		else if(timeB.contains(jogador))
			t = timeB;
		else
			assert false;
		
		return t;
	}
	
	public void resetJaTrucouGeral() {
		timeA.setJaTrucou(false);
		timeB.setJaTrucou(false);
	}
	
	public boolean isMaoDe11() {
		if(timeA.getPontos() == 11 || timeB.getPontos() == 11) {
			return true;
		} else {
			return false;
		}
	}
}
