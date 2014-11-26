package miquilini.felipe.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import miquilini.felipe.intermediario.status.LobbyStatus;
import miquilini.felipe.intermediario.status.MesaStatus;
import miquilini.felipe.servidor.exception.ComandoInexistenteException;
import miquilini.felipe.servidor.exception.ModalidadeIncoerente;
import miquilini.felipe.servidor.exception.NomeJaEmUsoException;
import miquilini.felipe.servidor.exception.TimesIndefinidos;
import miquilini.felipe.servidor.subsystem.mesa.Jogador;
import miquilini.felipe.servidor.subsystem.mesa.Mesa;
import miquilini.felipe.servidor.subsystem.mesa.Modalidade;
import miquilini.felipe.servidor.subsystem.mesa.Time;
import miquilini.felipe.servidor.subsystem.mesa.exception.APartidaJaEstaValendoMaxException;
import miquilini.felipe.servidor.subsystem.mesa.exception.EmPropDeTrucoException;
import miquilini.felipe.servidor.subsystem.mesa.exception.JaTrucouException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPodeTrucarMaoDe11Exception;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPodeTrucarNaPrimeiraRodadaException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoPossuiAVezException;
import miquilini.felipe.servidor.subsystem.mesa.exception.NaoRecebeuPropException;
import miquilini.felipe.servidor.subsystem.mesa.exception.PosicaoInvalidaException;
import miquilini.felipe.servidor.subsystem.rodada.IncobertoRodadaInicialException;

public class Servidor {
	private ServerSocket server;
	private final Map<Integer, Cliente> clientes = new HashMap<>();
	private int ultimoCodigo = -1;
	private boolean comSenha = false;
	private String senha;
	private Mesa mesa;
	private Modalidade modalidade;
	private final Set<String> nomesJaEmUso = new HashSet<>();
	public static final String delimiter = "<end>";
	
	public Servidor(int port, String senha, Modalidade modalidade) {
		this.modalidade = modalidade;
		if(senha != null && senha.length() > 0) {
			comSenha = true;
			this.senha = senha;
		}
		
		try {
			server = new ServerSocket(port);
			while(true) {
				Socket s = server.accept();
				int cod = ultimoCodigo + 1;
				Cliente cliente = new Cliente(s);
				clientes.put(cod, cliente);
				Runnable r = new EscutaCliente(cod, cliente);
				new Thread(r).start();
				ultimoCodigo++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fecharServer();
		}
	}
	
	private class EscutaCliente implements Runnable {
		
		private final Cliente cliente;
		private final int codigo;
		private boolean aceito = true;
		
		public EscutaCliente(int codigo, Cliente cliente) {
			this.cliente = cliente;
			this.codigo = codigo;
			
		}
		
		@Override
		public void run() {
			
			inicializa();

			if(!aceito) {
				//encaminharPara(codigo, "Acesso negado");
				finalizarCliente(codigo);
				return;
			}
			
			encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
			
			Scanner leitor = cliente.getLeitor();
			
			while(leitor.hasNext()) {
				String texto = leitor.next();
				if(texto.startsWith("<") && texto.endsWith(">")) {
					if(mesa != null && !(cliente.getEscolha().equals(TimeInfo.SEM_TIME))) {
						try {
							comandoTruco(codigo, texto);
						} catch (ComandoInexistenteException e) {
							encaminharPara(codigo, e.getMessage());
						}
					} else {
						try {
							comandoLobby(codigo, texto);
						} catch (ComandoInexistenteException e) {
							encaminharPara(codigo, e.getMessage());
						}
					}
				} else {
					encaminharParaTodos(cliente.getNome() + ": " + texto);
				}
			}
			finalizarCliente(codigo);
		}
		
		private void inicializa() {
			String nome = null;
			Scanner leitor = cliente.getLeitor();
				try {
					if(comSenha) {
						//avisar que existe senha
						encaminharPara(codigo, "<com senha>");
						{
							boolean primeiraVez = true;
							while(leitor.hasNext()) {
									if(primeiraVez) {
										String senhaUsuario = leitor.next();
										if(!senha.equals(senhaUsuario)) {
											aceito = false;
											return;
										}
											primeiraVez = false;
									} else {
										nome = leitor.next();
										if(nome == null || nomesJaEmUso.contains(nome)) {
											throw new NomeJaEmUsoException();
										} else {
											nomesJaEmUso.add(nome);
										}
										break;
									}
							}
						}
					} else {
						//avisar que não existe senha
						encaminharPara(codigo, "<sem senha>");
						while(leitor.hasNext()) {
							nome = leitor.next();
							if(nome == null || nomesJaEmUso.contains(nome)) {
								throw new NomeJaEmUsoException();
							} else {
								nomesJaEmUso.add(nome);
							}
							break;
						}
					}
				} catch(NoSuchElementException | IllegalStateException | NomeJaEmUsoException e) {
					aceito = false;
					encaminharPara(codigo, e.getMessage());
					return;
				}
				
				cliente.setNome(nome);
		}
		
	}
	
	private void criarMesa() throws TimesIndefinidos {
		{ //testa se é possivel criar a mesa
			//verifica a predisposicao dos times
			int timeA = 0;
			int timeB = 0;
			for(Integer i : clientes.keySet()) {
				Cliente c = clientes.get(i);
				if(c.getEscolha() == TimeInfo.A) {
					timeA++;
				} else if(c.getEscolha() == TimeInfo.B) {
					timeB++;
				}
			}
			
			if(timeA != timeB || timeA + timeB != modalidade.getQtdPlayers()) 
				throw new TimesIndefinidos();
		}
		
		mesa = new Mesa(modalidade);
		//os times serao definidos nestas duas variaveis
		Set<Jogador> tA = new HashSet<>();
		Set<Jogador> tB = new HashSet<>();
		
		//a ordem sera definida nesta outra
		Jogador[] jogadores = new Jogador[modalidade.getQtdPlayers()];
		//organiza os jogadores do timeA
		{
			int codigo = 0; //o time B tem os codigos pares
			for(Integer i : clientes.keySet()) {
				Cliente c = clientes.get(i);
				if(c.getEscolha() == TimeInfo.A) {
					Jogador j = new Jogador(mesa, codigo, c.getNome());
					c.setJogador(j);
					tA.add(j);
					jogadores[codigo] = j;
					codigo = codigo + 2; //somamos 2 para obter o proximo codigo par
				}
			}
		}
		{
			int codigo = 1; //o time B tem os codigos inpares
			for(Integer i : clientes.keySet()) {
				Cliente c = clientes.get(i);
				if(c.getEscolha() == TimeInfo.B) {
					Jogador j = new Jogador(mesa, codigo, c.getNome());
					c.setJogador(j);
					tB.add(j);
					jogadores[codigo] = j;
					codigo = codigo + 2; //somamos 2 para obter o proximo codigo impar
				}
			}
		}
		
		//organiza as variaveis prox e anterior de cada jogador
		//com auxilio da variavel jogadores
		for(int i = 0; i < jogadores.length; i++) {
			int prox = i + 1;
			if(prox == jogadores.length) {
				prox = 0;
			} else if(prox == -1) {
				prox = jogadores.length - 1;
			}
			int ant = i - 1;
			if(ant == jogadores.length) {
				ant = 0;
			} else if(ant == -1) {
				ant = jogadores.length - 1;
			}
			jogadores[i].setProxJogador(jogadores[prox]);
			jogadores[i].setJogadorAnterior(jogadores[ant]);
		}
		mesa.setTimeA(new Time(tA));
		mesa.setTimeB(new Time(tB));
		
		mesa.comecar();
		
		encaminhaStatusParaTodos();
	}
	
	private void finalizarCliente(int codigo) {
		Cliente cliente = clientes.get(codigo);
		cliente.fecharCliente();
		nomesJaEmUso.remove(cliente.getNome());
		clientes.remove(codigo);
		encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
		//aqui
		mesa = null;
	}
	
	private void encaminharPara(int cod, String texto) {
		PrintWriter pw = clientes.get(cod).getSaidaChat();
		pw.print(new StringBuilder(texto).append(delimiter).toString());
		pw.flush();
	}
	private void comandoLobby(int codigo, String comando) throws ComandoInexistenteException {
		Cliente c = clientes.get(codigo);
		switch(comando) {
			case "<timeA>":
				c.setEscolha(TimeInfo.A);
				encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
				break;
			case "<timeB>":
				c.setEscolha(TimeInfo.B);
				encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
				break;
			case "<comecar>":
			try {
				criarMesa();
			} catch (TimesIndefinidos e) {
				encaminharPara(codigo, e.getMessage());
			}
				break;
			case "<sair>":
				c.setEscolha(TimeInfo.SEM_TIME);
				encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
				break;
			default:
				throw new ComandoInexistenteException();
		}
		
		
	}
	private void comandoTruco(int codigo, String comando) throws ComandoInexistenteException {
		Jogador j = clientes.get(codigo).getJogador();
		try {
			switch(comando) {
				case "<trucar>":
					j.trucar();
					break;
				case "<aceitar>":
					j.aceitarTruco();
					break;
				case "<retrucar>":
					j.aceitarERevidarTruco();
					break;
				case "<fugir>":
					j.fugir();
					break;
				case "<1>":
				case "<2>":
				case "<3>":
					{
						int pos = Integer.parseInt(comando.substring(1, 2));
						j.jogarCarta(pos - 1, false);
					}
					break;
				case "<1incoberto>":
				case "<2incoberto>":
				case "<3incoberto>":
					{
						int pos = Integer.parseInt(comando.substring(1, 2));
						j.jogarCarta(pos - 1, true);
					}
					break;
				default:
					throw new ComandoInexistenteException();
			}
			if(mesa.getTimeA().isVitorioso()) {
				encaminharParaTodos("/////TIME A VENCEU\\\\\\\\\\");
				mesa = null;
				encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
			} else if(mesa.getTimeB().isVitorioso()) {
				encaminharParaTodos("/////TIME B VENCEU\\\\\\\\\\");
				mesa = null;
				encaminharParaTodos(LobbyStatus.converteParaString(getLobbyStatus()));
			} else {
				encaminhaStatusParaTodos();
			}
		} catch(NaoPossuiAVezException | APartidaJaEstaValendoMaxException |
				NaoPodeTrucarNaPrimeiraRodadaException | NaoRecebeuPropException |
				PosicaoInvalidaException | IncobertoRodadaInicialException |
				EmPropDeTrucoException | JaTrucouException  | NaoPodeTrucarMaoDe11Exception e) {
			encaminharPara(codigo, e.getMessage());
		} catch(ComandoInexistenteException e) {
			throw e;
		}
	}
	private void encaminhaStatusParaTodos() {
		for(Integer i : clientes.keySet()) {
			encaminharPara(i, MesaStatus.converteParaString(clientes.get(i).getJogador().getStatus()));
		}
	}
	private void encaminharParaTodos(String texto) {
		for(Integer cod : clientes.keySet()) {
			PrintWriter p = clientes.get(cod).getSaidaChat();
			p.print(new StringBuilder(texto).append(delimiter).toString());
			p.flush();
		}
	}
	
	public void fecharServer() {
		try {
			server.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private LobbyStatus getLobbyStatus() {
		List<String> nomes = new ArrayList<>();
		List<String> times = new ArrayList<>();
		{
			for(Integer i : clientes.keySet()) {
				Cliente c = clientes.get(i);
				nomes.add(c.getNome());
				times.add(c.getEscolha().toString());
			}
		}
		return new LobbyStatus(nomes, times);
	}
	
	public static void main(String[] args) {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.println("Porta do servidor: ");
			int porta = Integer.parseInt(scan.nextLine());
			System.out.println("Senha do servidor:");
			String senha = scan.nextLine();
			System.out.println("Modalidades:\n" +
					 "1 - Simples (2 jogadores)\n" + 
					 "2 - Dupla (4 jogadores)\n" + 
					 "3 - Douradinha (6 jogadores)\n" +
					 "Insira o código da modalidade a ser criada:");
			String m = scan.nextLine();
			scan.close();
			Modalidade modalidade = null;
			switch(m) {
				case "1":
					modalidade = Modalidade.SIMPLES;
					break;
				case "2":
					modalidade = Modalidade.DUPLA;
					break;
				case "3":
					modalidade = Modalidade.DOURADINHA;
					break;
				default:
					throw new ModalidadeIncoerente();
			}
			assert modalidade != null;
			System.out.println("Servidor rodando na porta " + porta);
			new Servidor(porta, senha, modalidade);
		} catch(NumberFormatException | ModalidadeIncoerente e) {
			System.out.println(e.getMessage());
		} 
	}
}
