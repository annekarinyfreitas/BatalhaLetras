package com.space;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ItemDetail {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ItemDetail window = new ItemDetail();
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
	public ItemDetail() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 385, 313);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblPblicos = new JLabel("Públicos");
		lblPblicos.setBounds(70, 58, 61, 16);
		frame.getContentPane().add(lblPblicos);
		
		JLabel lblPrivados = new JLabel("Privados");
		lblPrivados.setBounds(257, 58, 61, 16);
		frame.getContentPane().add(lblPrivados);
		
		JLabel lblItem = new JLabel("Item :");
		lblItem.setBounds(17, 6, 61, 16);
		frame.getContentPane().add(lblItem);
		
		JLabel lblVendedor = new JLabel("Vendedor : ");
		lblVendedor.setBounds(17, 30, 71, 16);
		frame.getContentPane().add(lblVendedor);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 44, 373, 12);
		frame.getContentPane().add(separator);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(16, 82, 164, 203);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(210, 82, 164, 203);
		frame.getContentPane().add(scrollPane_1);
		
		JTextArea textArea_1 = new JTextArea();
		scrollPane_1.setViewportView(textArea_1);
	}
}
