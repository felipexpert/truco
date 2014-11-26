package miquilini.felipe.intermediario.status;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import miquilini.felipe.intermediario.carta.Carta;

public class MesaStatus {
	private String meuNome;
	private List<Carta> minhaMao;
	private List<String> timeA;
	private List<String> timeB;
	private List<String> jogadoresNaOrdem;
	private int valendo;
	private String rodada;
	private Map<String, Carta> cartasJogadasNaRodada;
	private String meuTime;
	private int pontosTimeA;
	private int pontosTimeB;
	private Carta vira;
	
	private String jogadorAtual;
	private String log;
	
	public MesaStatus(String meuNome, List<Carta> minhaMao, List<String> timeA,
			List<String> timeB, List<String> jogadoresNaOrdem, int valendo,
			String rodada, Map<String, Carta> cartasJogadasNaRodada, String meuTime,
			int pontosTimeA, int pontosTimeB, String jogadorAtual, String log, 
			Carta vira) {
		super();
		this.meuNome = meuNome;
		this.minhaMao = minhaMao;
		this.timeA = timeA;
		this.timeB = timeB;
		this.jogadoresNaOrdem = jogadoresNaOrdem;
		this.valendo = valendo;
		this.rodada = rodada;
		this.cartasJogadasNaRodada = cartasJogadasNaRodada;
		this.meuTime = meuTime;
		this.pontosTimeA = pontosTimeA;
		this.pontosTimeB = pontosTimeB;
		this.jogadorAtual = jogadorAtual;
		this.log = log;
		this.vira = vira;
	}
	
	public String getMeuNome() {
		return meuNome;
	}
	public Carta getVira() {
		return vira;
	}

	public String getJogadorAtual() {
		return jogadorAtual;
	}

	public String getLog() {
		return log;
	}

	public List<Carta> getMinhaMao() {
		return minhaMao;
	}
	public List<String> getTimeA() {
		return timeA;
	}
	public List<String> getTimeB() {
		return timeB;
	}
	public List<String> getJogadoresNaOrdem() {
		return jogadoresNaOrdem;
	}
	public int getValendo() {
		return valendo;
	}
	public String getRodada() {
		return rodada;
	}
	public Map<String, Carta> getCartasJogadasNaRodada() {
		return cartasJogadasNaRodada;
	}
	public String getMeuTime() {
		return meuTime;
	}
	public int getPontosTimeA() {
		return pontosTimeA;
	}
	public int getPontosTimeB() {
		return pontosTimeB;
	}
	
	public static MesaStatus converteParaStatus(String texto) throws IllegalArgumentException {
		MesaStatus st = null;
		Scanner scan = null;
		try {
			String t = texto.substring(8);
			scan = new Scanner(t);
			scan.useDelimiter(";");
			String meuNome = scan.next();
			List<Carta> minhaMao = new ArrayList<>();
			{
				String minhaMao2 = scan.next();
				Scanner s = new Scanner(minhaMao2);
				s.useDelimiter(":");
				while(s.hasNext()) {
					minhaMao.add(Carta.getCarta(s.next()));
				}
				s.close();
			}
			List<String> timeA = new ArrayList<>();
			{
				String timeA2 = scan.next();
				Scanner s = new Scanner(timeA2);
				s.useDelimiter(":");
				while(s.hasNext()) {
					timeA.add(s.next());
				}
				s.close();
			}
			List<String> timeB = new ArrayList<>();
			{
				String timeB2 = scan.next();
				Scanner s = new Scanner(timeB2);
				s.useDelimiter(":");
				while(s.hasNext()) {
					timeB.add(s.next());
				}
				s.close();
			}
			List<String> jogadoresNaOrdem = new ArrayList<>();
			{
				String jogadoresNaOrdem2 = scan.next();
				Scanner s = new Scanner(jogadoresNaOrdem2);
				s.useDelimiter(":");
				while (s.hasNext()) {
					jogadoresNaOrdem.add(s.next());					
				}
				s.close();
			}
			int valendo = scan.nextInt();
			String rodada = scan.next();
			Map<String, Carta> cartasJogadasNaRodada = new LinkedHashMap<>();
			{
				String cartasJogadasNaRodada2 = scan.next();
				Scanner s = new Scanner(cartasJogadasNaRodada2);
				s.useDelimiter(":");
				while(s.hasNext()) {
					cartasJogadasNaRodada.put(s.next(), Carta.getCarta(s.next()));
				}
				s.close();
			}
			String meuTime = scan.next();
			int pontosTimeA = scan.nextInt();
			int pontosTimeB = scan.nextInt();
			String jogadorAtual = scan.next();
			String log = scan.next();
			Carta vira = Carta.getCarta(scan.next());
			
			st = new MesaStatus(meuNome, minhaMao, timeA, timeB, jogadoresNaOrdem, valendo, rodada, cartasJogadasNaRodada, meuTime, pontosTimeA, pontosTimeB, jogadorAtual, log, vira);
			
		} catch(Exception e) {
			System.out.println(e);
			throw new IllegalArgumentException("texto incoerente");
		} finally {
			if(scan != null)
				scan.close();
		}
		assert st != null;
		return st;
	}
	
	public static String converteParaString(MesaStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("<status>");
		sb.append(status.meuNome).append(';');
		for(Carta c : status.minhaMao) {
			sb.append(c).append(':');
		}
		sb.append(';');
		for(String s : status.timeA) {
			sb.append(s).append(':');
		}
		sb.append(';');
		for(String s : status.timeB) {
			sb.append(s).append(':');
		}
		sb.append(';');
		for(String s : status.jogadoresNaOrdem) {
			sb.append(s).append(':');
		}
		sb.append(';');
		sb.append(status.valendo).append(';');
		sb.append(status.rodada).append(';');
		for(String j : status.cartasJogadasNaRodada.keySet()) {
			sb.append(j).append(':').append(status.cartasJogadasNaRodada.get(j).toString()).append(':');
		}
		sb.append(';');
		sb.append(status.meuTime).append(';');
		sb.append(status.pontosTimeA).append(';');
		sb.append(status.pontosTimeB).append(';');
		sb.append(status.jogadorAtual).append(';');
		sb.append(status.log).append(';');
		sb.append(status.vira);
		
		return sb.toString();
	}
}
