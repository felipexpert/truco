package miquilini.felipe.clienteSwing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import miquilini.felipe.clienteSwing.ClienteSwing;

public class FrmDisplay extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7544516361457794133L;
	
	
	private JPanel contentPane;
	private JTextField txtEnviar;
	private JTextArea txtChat;
	private JTextArea txtStatus;
	private JTextArea txtLog;
	private JButton btnEnviar;
	private ClienteSwing cliente;

	/**
	 * Create the frame.
	 */
	public FrmDisplay(final ClienteSwing cliente) {
		this.cliente = cliente;
		setTitle("Truco dos Amigos");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(4, 4));
		setContentPane(contentPane);
		
		JPanel pnlCentro = new JPanel();
		contentPane.add(pnlCentro, BorderLayout.CENTER);
		pnlCentro.setLayout(new GridLayout(1, 0, 4, 0));
		
		JPanel pnlStatus = new JPanel();
		pnlCentro.add(pnlStatus);
		pnlStatus.setLayout(new BorderLayout(0, 0));
		
		txtStatus = new JTextArea();
		txtStatus.setEditable(false);
		JScrollPane spStatus = new JScrollPane(txtStatus);
		pnlStatus.add(spStatus, BorderLayout.CENTER);
		
		JLabel lblStatus = new JLabel("Status");
		pnlStatus.add(lblStatus, BorderLayout.NORTH);
		
		JPanel pnlLog = new JPanel();
		pnlCentro.add(pnlLog);
		pnlLog.setLayout(new BorderLayout(0, 0));
		
		txtLog = new JTextArea();
		txtLog.setEditable(false);
		JScrollPane spLog = new JScrollPane(txtLog);
		pnlLog.add(spLog, BorderLayout.CENTER);
		
		JLabel lblLog = new JLabel("Log");
		pnlLog.add(lblLog, BorderLayout.NORTH);
		
		JPanel pnlSul = new JPanel();
		contentPane.add(pnlSul, BorderLayout.SOUTH);
		pnlSul.setLayout(new BorderLayout(0, 4));
		
		JPanel pnlEnviar = new JPanel();
		pnlSul.add(pnlEnviar, BorderLayout.SOUTH);
		pnlEnviar.setLayout(new BorderLayout(0, 0));
		
		btnEnviar = new JButton("ENVIAR");
		btnEnviar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enviar();
			}
		});
		pnlEnviar.add(btnEnviar, BorderLayout.EAST);
		
		txtEnviar = new JTextField();
		txtEnviar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enviar();				
			}
		});
		pnlEnviar.add(txtEnviar, BorderLayout.CENTER);
		txtEnviar.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Direitos intelectuais reservados a Felipe Carmona Miquilini");
		pnlEnviar.add(lblNewLabel, BorderLayout.SOUTH);
		
		JPanel pnlNorte = new JPanel();
		pnlNorte.setPreferredSize(new Dimension(0, 90));
		contentPane.add(pnlNorte, BorderLayout.NORTH);
		pnlNorte.setLayout(new BorderLayout(0, 0));
		
		txtChat = new JTextArea();
		txtChat.setEditable(false);
		JScrollPane spChat = new JScrollPane(txtChat);
		pnlNorte.add(spChat, BorderLayout.CENTER);
		
		JLabel lblChat = new JLabel("Chat");
		pnlNorte.add(lblChat, BorderLayout.WEST);
		
		txtEnviar.requestFocus();
		
	}
	
	private void enviar() {
		String texto = txtEnviar.getText();
		if(!texto.equals("") && !texto.equals("/help")) {
			cliente.enviar(texto);
		} else if(texto.equals("/help")) {
			txtChat.setText(
					new StringBuilder("------------COMANDOS DO LOBBY------------\n").
					append("<timeA> -> Entra em A\n").
					append("<timeB> -> Entra em B\n").
					append("<sair> -> Vai para o status \"sem time\"\n").
					append("<comecar> -> Começa o jogo\n").
					append("------------COMANDOS DO TRUCO------------\n").
					append("<trucar> -> Truca\n").
					append("<aceitar> -> Se for trucado, este comando aceita a proposta\n").
					append("<retrucar> -> Aceita e revida\n").
					append("<fugir> -> Foge (válido apenas se for trucado)\n").
					append("<1> -> Joga a primeira carta\n").
					append("<2> -> Joga a segunda carta\n").
					append("<3> -> Joga a terceira carta\n").
					append("<1incoberto> -> Joga a primeira carta incoberta\n").
					append("<2incoberto> -> Joga a segunda carta incoberta\n").
					append("<3incoberto> -> Joga a terceira carta incoberta\n").toString()
					);
		}
		
		texto = null;
		txtEnviar.setText("");
		txtEnviar.requestFocus();
	}
	
	public void deixarVisivel() {
		setVisible(true);
	}
	
	public void updateStatus(String texto) {
		txtStatus.setText(texto);
	}
	
	public void updateLog(String texto) {
		txtLog.setText(texto);
		
		txtLog.setCaretPosition(txtLog.getDocument().getLength());
	}
	
	public void addChat(String texto) {
		txtChat.append(texto + '\n');
		txtChat.setCaretPosition(txtChat.getDocument().getLength());
	}

}
