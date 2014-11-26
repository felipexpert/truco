package miquilini.felipe.clienteSwing;

import java.io.IOException;

import javax.swing.JOptionPane;

import miquilini.felipe.cliente.Cliente;
import miquilini.felipe.clienteSwing.gui.FrmDisplay;
import miquilini.felipe.clienteSwing.som.SFCoinEffect;
import miquilini.felipe.intermediario.carta.Carta;
import miquilini.felipe.intermediario.status.LobbyStatus;
import miquilini.felipe.intermediario.status.MesaStatus;
import miquilini.felipe.servidor.Servidor;

public class ClienteSwing extends Cliente{
	
	private FrmDisplay display;
	
	public ClienteSwing(String ip, int porta) {
		super(ip, porta);
	}
	
	@Override
	public void output(String texto) {
		JOptionPane.showMessageDialog(null, texto);
	};
	
	public void enviar(String texto) {
		String t = texto + Servidor.delimiter;
		getEscritor().print(t);
		getEscritor().flush();
	}
	
	@Override
	public Runnable criaEscutaServidor() {
		return new EscutaServidor();
	};
	
	private class EscutaServidor implements Runnable{

		@Override
		public void run() {
			display.setVisible(true);
			tentaConectar();
			
			while(getLeitor().hasNext()) {
				String texto = getLeitor().next();
				if(texto.startsWith("<status>")) {
					encaminharStatus(MesaStatus.converteParaStatus(texto));
				} else if(texto.startsWith("<lobbystatus>")) {
					encaminharLobbyStatus(LobbyStatus.converteParaLobbyStatus(texto));
				} else {
					encaminharChat(texto);
				}
			}
			
			System.exit(0);
		}
		
		private void tentaConectar() {
			while(getLeitor().hasNext()) {
				String senha = getLeitor().next();
				if(senha.equals("<sem senha>")) {
					enviar(JOptionPane.showInputDialog(null, "Identificação:"));
				} else {
					enviar(JOptionPane.showInputDialog(null, "Senha do server:"));
					enviar(JOptionPane.showInputDialog(null, "Identificação:"));
				}
				break;
			}
		}
	}
	
	public void criaDisplay() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				display = new FrmDisplay(ClienteSwing.this);
			}
			
		}).start();
	}
	
	public void encaminharStatus(MesaStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("----- Bem-vindo(a) ").append(status.getMeuNome()).append(", seu time é o ").append(status.getMeuTime()).append(" -----").append("\n\n");
		sb.append("Time A").append('\n');
		for(String n : status.getTimeA()) {
			sb.append("   ").append(n).append('\n');
		}
		sb.append("Time B").append('\n');
		for(String n : status.getTimeB()) {
			sb.append("   ").append(n).append('\n');
		}
		sb.append("Ordem dos jogadores").append('\n');
		for(String n : status.getJogadoresNaOrdem()) {
			sb.append("   ").append(n).append('\n');
		}
		sb.append("O Time A tem ").append(status.getPontosTimeA()).append(" ponto(s)\n");
		sb.append("O Time B tem ").append(status.getPontosTimeB()).append(" ponto(s)\n");
		sb.append('\n');
		sb.append(status.getRodada()).append('\n');
		sb.append("Valendo: ").append(status.getValendo()).append('\n');
		if(status.getCartasJogadasNaRodada().size() == 0) {
			sb.append("Nenhuma carta foi jogada na rodada atual").append('\n');
		} else {
			sb.append("Cartas jogadas nesta rodada").append('\n');
			for(String j : status.getCartasJogadasNaRodada().keySet()) {
				sb.append("   ").append(j).append(" jogou ").append(status.getCartasJogadasNaRodada().get(j)).append('\n');
			}
		}
		sb.append(status.getJogadorAtual()).append(" possui a vez").append('\n').append('\n');
		sb.append(status.getVira()).append(" é a carta vira").append('\n');
		sb.append("Minha Mão").append('\n');
		for(Carta c : status.getMinhaMao()) {
			sb.append("   ").append(c.toString()).append('\n');
		}
		
		display.updateStatus(sb.toString());
		
		display.updateLog(status.getLog());
		
		if(!status.getMeuNome().equals(status.getJogadorAtual()))
			somAlguemJogou();
		else
			somMinhaVez();
	}
	
	public void somAlguemJogou() {
		SFCoinEffect.play();
	}
	
	public void somMinhaVez() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < 3; i++) {
					if(i > 0) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					somAlguemJogou();
				}				
			}
		}).start();
	}
	
	public void encaminharLobbyStatus(LobbyStatus status) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < status.getNomes().size(); i++) {
			sb.append(status.getNomes().get(i)).append(" - ").append(status.getTimes().get(i)).append('\n');
		}
		display.updateStatus(sb.toString());
		display.updateLog("");
	}
	
	public void encaminharChat(String texto) {
		display.addChat(texto);
	}
	
	public static void main(String[] args) {
		try {
			String ip = JOptionPane.showInputDialog("ip: ");
			int porta = Integer.parseInt(JOptionPane.showInputDialog("porta"));
			new ClienteSwing(ip, porta);
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Não foi possivel conectar com o servidor");
		} 
	}
	
	public void fechar() {
		try {
			getSocket().close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
