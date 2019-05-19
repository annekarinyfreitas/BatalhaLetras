package com.space;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import br.edu.ifce.space.Message;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class Client {

	JFrame frame;
	private JTextField textField;
	private JTable table;
	private String name;
	private ArrayList <Item> items;
	private JavaSpace space;
	private JTextArea log;

	/**
	 * Create the application.
	 */
	public Client(String name, JavaSpace space, JTextArea log) {
		this.name = name;
		this.space = space;
		this.log = log;
		initialize(name);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(final String name) {
		frame = new JFrame();
		frame.setTitle(name);
		frame.setResizable(false);
		frame.setBounds(100, 100, 540, 355);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblItensParaLeilo = new JLabel("Itens para leilão");
		lblItensParaLeilo.setBounds(40, 6, 450, 16);
		lblItensParaLeilo.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblItensParaLeilo);
		
		JButton btnNewButton = new JButton("Inserir");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewItem();
			}
		});
		
		btnNewButton.setBounds(375, 298, 159, 29);
		frame.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(6, 298, 357, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Inserir novo item");
		lblNewLabel.setBounds(16, 281, 113, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 29, 528, 240);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
	}
		
	private Leilao readLeilaoFromSpace(JavaSpace space) {
		Leilao leilaoTemplate = new Leilao();
		try {
			Leilao leilao = (Leilao) space.take(leilaoTemplate, null, 60 * 1000);
			return leilao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null; 
	}
	
	private String formattedLeilaoItems(ArrayList<Item> items) {
		String r = "";
		
		for (Item i: items) {
			r = r + i.author + " " + i.description + " " + "\n";
		}
		return r;
	}
	
	private void addNewItem() {
		Item i = new Item();
		i.author = name;
		i.description = textField.getText();
		
		Leilao leilao = readLeilaoFromSpace(space);
		leilao.items.add(i);
		
		try {
			space.write(leilao, null, 600 * 1000);
		} catch (RemoteException | TransactionException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Items");
		System.out.println(formattedLeilaoItems(leilao.items));
		this.textField.setText("");
		
		updateLogWithMessage("Item " + i.description + " adicionado por " + i.author);
	}
	
	private void updateLogWithMessage(String message) {
		log.setText(log.getText() + message + "\n");
	}
}
