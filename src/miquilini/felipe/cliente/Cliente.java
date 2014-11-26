package miquilini.felipe.cliente;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import miquilini.felipe.intermediario.status.LobbyStatus;
import miquilini.felipe.intermediario.status.MesaStatus;
import miquilini.felipe.servidor.Servidor;

public abstract class Cliente {
	
	private Socket socket;
	private PrintWriter escritor;
	private Scanner leitor;
	
	public Cliente(String ip, int porta) {
		criaDisplay();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			socket = new Socket(ip, porta);
			
			escritor = new PrintWriter(socket.getOutputStream());
			leitor = new Scanner(socket.getInputStream());
			leitor.useDelimiter("<end>");
			
			Runnable r = criaEscutaServidor();
			new Thread(r).start();
		} catch (IOException e) {
			output(e.getLocalizedMessage());
		} 
	}
	
	public abstract void output(String texto);
	
	public abstract Runnable criaEscutaServidor();
	
	public void enviar(String texto) {
		String t = texto + Servidor.delimiter;
		escritor.print(t);
		escritor.flush();
	}
	
	public  abstract void criaDisplay();
	
	public abstract void encaminharStatus(MesaStatus status);
	
	public abstract void somAlguemJogou();
	
	public abstract void somMinhaVez();
	
	public abstract void encaminharLobbyStatus(LobbyStatus status);
	
	public abstract void encaminharChat(String texto);
	
	public void fechar() {
		try {
			socket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getEscritor() {
		return escritor;
	}

	public Scanner getLeitor() {
		return leitor;
	}	
}
