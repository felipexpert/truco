package miquilini.felipe.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import miquilini.felipe.servidor.subsystem.mesa.Jogador;

public class Cliente {
	private Socket socket;
	private String nome;
	//private ObjectOutputStream saidaStatus;
	private PrintWriter saidaChat;
	private Scanner leitor;
	private Jogador jogador;
	private TimeInfo escolha = TimeInfo.SEM_TIME;
	
	public Cliente(Socket socket) throws IOException {
		this.socket = socket;
		//saidaStatus = new ObjectOutputStream(socket.getOutputStream());
		saidaChat = new PrintWriter(socket.getOutputStream());
		leitor = new Scanner(socket.getInputStream());
		leitor.useDelimiter(Servidor.delimiter);
	}
	
	public TimeInfo getEscolha() {
		return escolha;
	}
	
	public String getNome() {
		return nome;
	}

	//public ObjectOutputStream getSaidaStatus() {
	//	return saidaStatus;
	//}

	public PrintWriter getSaidaChat() {
		return saidaChat;
	}

	public Scanner getLeitor() {
		return leitor;
	}

	public Jogador getJogador() {
		return jogador;
	}
	
	public void setEscolha(TimeInfo escolha) {
		this.escolha = escolha;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setJogador(Jogador jogador) {
		this.jogador = jogador;
	}
	
	public void fecharCliente() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(leitor != null)
			leitor.close();
		if(saidaChat != null)
			saidaChat.close();
		/*
		if(saidaStatus != null) {
			try {
				saidaStatus.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
}
