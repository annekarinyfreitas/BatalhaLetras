package com.space;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ItemDetail {

	JFrame frame;
	private JLabel lblItem;
	private JLabel lblVendedor;
	private Item item;
	private JTextArea publicTextArea;
	private JTextArea privateTextArea;
	private String clientName;

	/**
	 * Create the application.
	 */
	public ItemDetail(Item item, String clientName) {
		this.item = item;
		this.clientName = clientName;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 385, 313);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblPblicos = new JLabel("Públicos");
		lblPblicos.setBounds(70, 58, 61, 16);
		frame.getContentPane().add(lblPblicos);
		
		JLabel lblPrivados = new JLabel("Privados");
		lblPrivados.setBounds(257, 58, 61, 16);
		frame.getContentPane().add(lblPrivados);
		
		lblItem = new JLabel("Item :");
		lblItem.setBounds(17, 6, 362, 16);
		frame.getContentPane().add(lblItem);
		
		lblVendedor = new JLabel("Vendedor : ");
		lblVendedor.setBounds(17, 30, 362, 16);
		frame.getContentPane().add(lblVendedor);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 44, 373, 12);
		frame.getContentPane().add(separator);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 82, 164, 203);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		publicTextArea = textArea;
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(210, 82, 164, 203);
		frame.getContentPane().add(scrollPane_1);
		
		JTextArea textArea_1 = new JTextArea();
		scrollPane_1.setViewportView(textArea_1);
		privateTextArea = textArea_1;
		
		this.lblItem.setText("Item: " + item.description);
		this.lblVendedor.setText("Vendedor: " + item.author);
		
		updateOffers();
	}
	
	private String updateOffers() {
		String r = "";
		
		if (item.offers != null && item.offers.isEmpty() == false) {
			for (Offer o: item.offers) {
				if (o.isPublic == "1") {
					publicTextArea.setText(publicTextArea.getText() + "Autor: " + o.author + "| Valor: " + o.value + "\n");
				} else {
					if (item.author.equals(clientName) || o.author.equals(clientName)) {
						privateTextArea.setText(privateTextArea.getText() + "Autor: " + o.author + "| Valor: " + o.value + "\n");
					}
				}
			}
		}
		
		return r;
	}
}
