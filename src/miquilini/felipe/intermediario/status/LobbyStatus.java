package miquilini.felipe.intermediario.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LobbyStatus {
	private List<String> nomes;
	private List<String> times;
	public LobbyStatus(List<String> nomes, List<String> times) {
		super();
		this.nomes = nomes;
		this.times = times;
	}
	public List<String> getNomes() {
		return nomes;
	}
	public List<String> getTimes() {
		return times;
	}
	public static LobbyStatus converteParaLobbyStatus(String texto) throws IllegalArgumentException{
		Scanner scan = null;
		List<String> nomes = null;
		List<String> times = null;
		try {
			String t = texto.substring(13);
			scan = new Scanner(t);
			scan.useDelimiter(";");
			nomes = new ArrayList<>();
			{
				String nomes1 = scan.next();
				Scanner s = new Scanner(nomes1);
				s.useDelimiter(":");
				while(s.hasNext()) {
					nomes.add(s.next());
				}
				s.close();
			}
			times = new ArrayList<>();
			{
				String times1 = scan.next();
				Scanner s = new Scanner(times1);
				s.useDelimiter(":");
				while(s.hasNext()) {
					times.add(s.next());
				}
				s.close();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("texto incoerente");
		} finally {
			if(scan != null)
				scan.close();
		}
		assert nomes != null && times != null;
		return new LobbyStatus(nomes, times);
	}
	
	public static String converteParaString(LobbyStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("<lobbystatus>");
		for(String n : status.nomes) {
			sb.append(n).append(':');
		}
		sb.append(';');
		for(String t : status.times) {
			sb.append(t).append(':');
		}
		return sb.toString();
	}
}
