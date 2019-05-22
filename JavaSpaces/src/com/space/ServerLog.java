package com.space;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class ServerLog {

	private JFrame frame;
	private JTextField textField;
	private JavaSpace space;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerLog window = new ServerLog();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerLog() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblLeilo = new JLabel("Leilão");
		lblLeilo.setBounds(188, 6, 61, 16);
		frame.getContentPane().add(lblLeilo);
		
		JLabel lblAdicionarCliente = new JLabel("Adicionar cliente");
		lblAdicionarCliente.setBounds(6, 222, 106, 16);
		frame.getContentPane().add(lblAdicionarCliente);
		
		textField = new JTextField();
		textField.setBounds(4, 239, 311, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnAdicionar = new JButton("Adicionar");
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertNewClient();
			}
		});
		
		btnAdicionar.setBounds(327, 239, 117, 29);
		frame.getContentPane().add(btnAdicionar);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 34, 438, 185);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textAreaLog = new JTextArea();
		this.textArea = textAreaLog;
		scrollPane.setViewportView(textAreaLog);
		
		// Já deixa salvo o space para os clientes
		lookupSpace();
		writeNewLeilao();
	}
	
	// Pesquisa o space e salva na variável desta classe
	public void lookupSpace() {
		System.out.println("Procurando pelo servico Java Space");
		Lookup finder = new Lookup(JavaSpace.class);
		JavaSpace space = (JavaSpace) finder.getService();
		
		if (space == null) {
			System.out
					.println("O servico JavaSpace nao foi encontrado. Encerrando...");
			System.exit(-1);
		}
		
		Leilao leilaoTemplate = new Leilao();
		try {
			// Remove algum leilao que esteja ocorrendo
			Leilao leilao = (Leilao) space.take(leilaoTemplate, null, 60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		System.out.println("O servico JavaSpace foi encontrado.");
		this.space = space;
	}
	
	// Inicializa um array de itens para o Leilao
	private void writeNewLeilao() {
		Leilao novoLeilao = new Leilao();
		novoLeilao.items = new ArrayList<Item>();
		
		try {
			this.space.write(novoLeilao, null, 600 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	private void insertNewClient() {
		Client client = new Client(textField.getText(), space, textArea);
		client.frame.setVisible(true);
		
		textField.setText("");
	}
}
