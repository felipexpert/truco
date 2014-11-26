package miquilini.felipe.servidor.subsystem.mesa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import miquilini.felipe.intermediario.carta.Carta;
import miquilini.felipe.intermediario.status.MesaStatus;
import miquilini.felipe.servidor.subsystem.mesa.exception.APartidaJaEstaValendoMaxException;
import miquilini.felipe.servidor.subsystem.mesa.exception.EmPropDeTrucoException;
import miquilini.felipe.servidor.subsystem.mesa.exception.JaTrucouException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPodeTrucarMaoDe11Exception;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPodeTrucarNaPrimeiraRodadaException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPossuiAVezException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoRecebeuPropException;
import miquilini.felipe.servidor.subsystem.mesa.exception.PosicaoInvalidaException;
import miquilini.felipe.servidor.subsystem.rodada.IncobertoRodadaInicialException;

public class Jogador implements Comparable<Jogador>{
	private final Mesa mesa;
	private final int codigo;
	private final String nome;
	private List<Carta> cartasNaMao;
	private boolean temAVez = false;
	private boolean foiTrucado = false;
	//private boolean jaTrucou = false;
	private Jogador proxJogador;
	private Jogador jogadorAnterior;
	
	public Jogador(final Mesa mesa, final int codigo, final String nome) {
		this.mesa = mesa;
		this.codigo = codigo;
		this.nome = nome;
		if(codigo == 0)
			temAVez = true;
	}
	
	/*public boolean jaTrucou() {
		return jaTrucou;
	}

	public void setJaTrucou(boolean jaTrucou) {
		this.jaTrucou = jaTrucou;
	}*/
	
	public int getCodigo() {
		return codigo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public Jogador getProxJogador() {
		return proxJogador;
	}
	
	public Jogador getJogadorAnterior() {
		return jogadorAnterior;
	}
	
	public void setMao(List<Carta> cartas) {
		cartasNaMao = cartas;
	}
	
	private void receberPropDeTruco() {
		foiTrucado = true;
	}
	
	public boolean foiTrucado() {
		return foiTrucado;
	}
	
	public void aceitarERevidarTruco() throws NaoRecebeuPropException, APartidaJaEstaValendoMaxException, NaoPodeTrucarNaPrimeiraRodadaException, NaoPodeTrucarMaoDe11Exception {
		if(mesa.getValendo() == Time.vitoria - 3)
			throw new APartidaJaEstaValendoMaxException("Apenas digite <aceitar>, que a partida valerá 12");
		if(mesa.isMaoDe11())
			throw new NaoPodeTrucarMaoDe11Exception();
		
		aceitar();
		mesa.proporTruco();
		jogadorAnterior.receberPropDeTruco();
		StringBuilder sb = new StringBuilder();
		sb.append('\n').append(this.nome).append(" aceita truco e retruca para ").append(jogadorAnterior.nome).append('.');
		mesa.addLog(sb.toString());
	}
	
	public void aceitarTruco() throws NaoRecebeuPropException {
		aceitar();
		StringBuilder sb = new StringBuilder();
		sb.append('\n').append(this.nome).append(" aceita.");
		mesa.addLog(sb.toString());
	}
	
	private void aceitar() throws NaoRecebeuPropException {
		if(!foiTrucado)
			throw new NaoRecebeuPropException();
		
		mesa.aceitarTruco();
		foiTrucado = false;
	}
	
	public void fugir() throws NaoRecebeuPropException {
		if(!foiTrucado)
			throw new NaoRecebeuPropException();
		
		foiTrucado = false; //Com este -->
		mesa.fugir(this); // Mudei aqui <--
		StringBuilder sb = new StringBuilder();
		sb.append('\n').append(this.nome).append(" foge.");
		mesa.addLog(sb.toString());
	}
	
	public void trucar() throws NaoPossuiAVezException, APartidaJaEstaValendoMaxException, NaoPodeTrucarNaPrimeiraRodadaException, JaTrucouException, NaoPodeTrucarMaoDe11Exception {
		if(!temAVez)
			throw new NaoPossuiAVezException();
		if(mesa.getValendo() == Time.vitoria)
			throw new APartidaJaEstaValendoMaxException("A partida já está valendo 12");
		if(mesa.getTime(this).getJaTrucou()) 
			throw new JaTrucouException();
		if(mesa.isMaoDe11()) 
			throw new NaoPodeTrucarMaoDe11Exception();
		mesa.proporTruco();
		proxJogador.receberPropDeTruco();
		mesa.getTime(this).setJaTrucou(true);
		StringBuilder sb = new StringBuilder();
		sb.append('\n').append(this.nome).append(" truca ").append(proxJogador.nome).append('.');
		mesa.addLog(sb.toString());
	}
	
	public void receberPropMaoDe11() {
		mesa.proporMaoDe11();
		receberPropDeTruco();
		mesa.addLog(new StringBuilder().append('\n').append(nome).append(" recebe proposta de mão de 11\n(se aceitar a queda valerá 3, se fugir, o time adversário ganhará 1)").toString());
	}
	
	public void jogarCarta(final int posicao, boolean incoberto) throws NaoPossuiAVezException, PosicaoInvalidaException, IncobertoRodadaInicialException, EmPropDeTrucoException{
		if(!temAVez)
			throw new NaoPossuiAVezException();
		if(posicao < 0 || posicao >= cartasNaMao.size())
			throw new PosicaoInvalidaException();
		
		Carta escolhida = cartasNaMao.get(posicao);
		try {
		temAVez = false;
		proxJogador.setTemAVez(true);
		
		cartasNaMao.remove(posicao);
		if(!incoberto) {
			mesa.jogar(escolhida, this);
		} else {
			try {
				mesa.jogar(Carta.INCOBERTO, this);
			} catch(IncobertoRodadaInicialException e) {
				temAVez = true;
				proxJogador.setTemAVez(false);
				cartasNaMao.add(escolhida);
				throw e;
			}
		}
		} catch (EmPropDeTrucoException e) {
			temAVez = true;
			proxJogador.setTemAVez(false);
			cartasNaMao.add(escolhida);
			throw e;
		}
	}
	public void setTemAVez(boolean b) {
		temAVez = b;
	}
	
	public boolean temAVez() {
		return temAVez;
	}
	public void setJogadorAnterior(Jogador jogador) {
		jogadorAnterior = jogador;
	}
	public void setProxJogador(Jogador jogador) {
		proxJogador = jogador;
	}
	
	public MesaStatus getStatus() {
		
		List<String> timeA = new ArrayList<>();
		{
			Set<Jogador> timeA2 = mesa.getTimeA().getJogadores();
			for(Jogador j : timeA2) {
				timeA.add(j.nome);
			}
		}
		List<String> timeB = new ArrayList<>();
		{
			Set<Jogador> timeB2 = mesa.getTimeB().getJogadores();
			for(Jogador j : timeB2) {
				timeB.add(j.nome);
			}
		}
		List<String> jogadoresNaOrdem = new ArrayList<>();
		{
			for(Jogador j : mesa.getJogadoresNaOrdem()) {
				jogadoresNaOrdem.add(j.nome);
			}
		}
		
		String meuTime = null;
		if(mesa.getTimeA().contains(this))
			meuTime = "Time A";
		else
			meuTime = "Time B";
		assert meuTime != null;
		
		String jogadorAtual = mesa.getJogadorAtual().getNome();
		String log = mesa.getLog();
		Carta vira = mesa.getVira();
		
		MesaStatus status = new MesaStatus(nome, cartasNaMao, timeA, timeB,
				jogadoresNaOrdem, mesa.getValendo(),
				mesa.getRodadaAtual().toString(),
				mesa.getCartasJogadasNaRodadaAtual(), meuTime, mesa.getTimeA().getPontos(),
				mesa.getTimeB().getPontos(), jogadorAtual, log, vira);
		
		return status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof Jogador)) 
			return false;
		
		return codigo == ((Jogador)obj).codigo;
	}
	
	@Override
	public int hashCode() {
		return codigo;
	}

	@Override
	public int compareTo(Jogador o) {
		return codigo - o.codigo;
	}
}
