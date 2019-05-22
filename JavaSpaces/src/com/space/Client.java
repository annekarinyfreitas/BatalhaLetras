package com.space;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Desktop.Action;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import br.edu.ifce.space.Message;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class Client {

	JFrame frame;
	private JTextField textField;
	private DefaultTableModel tableModel;
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
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblItensParaLeilo = new JLabel("Itens para leilão");
		lblItensParaLeilo.setBounds(161, 6, 171, 16);
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
		
		String col[] = {"ID", "Descricao", "Vendedor", " ", " "};
		tableModel = new DefaultTableModel(col, 0);
		
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		
		AbstractAction infoAction = new AbstractAction() {
		    public void actionPerformed(ActionEvent e)
		    {
		        JTable table = (JTable)e.getSource();
		        int modelRow = Integer.valueOf( e.getActionCommand() );
		        openInfoFromItem(items.get(modelRow));
		        
		        //((DefaultTableModel)table.getModel()).removeRow(modelRow);
		    }
		};
		
		AbstractAction offerAction = new AbstractAction() {
		    public void actionPerformed(ActionEvent e)
		    {
		        JTable table = (JTable)e.getSource();
		        int modelRow = Integer.valueOf( e.getActionCommand() );
		        Item item = items.get(modelRow);
		        
		        if (item.author.equals(name) == false) {
		        	offerToItem(items.get(modelRow));
		        } else {
		        	JOptionPane.showMessageDialog(null, "Você não pode dar lances aos seus próprios itens");
		        }
		        
		        //((DefaultTableModel)table.getModel()).removeRow(modelRow);
		    }
		};
		 
		ButtonColumn infoButton = new ButtonColumn(table, infoAction, 3);
		infoButton.setMnemonic(KeyEvent.VK_D);
		
		ButtonColumn offerButton = new ButtonColumn(table, offerAction, 4);
		
		JButton btnNewButton_1 = new JButton("Atualizar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Leilao leilao = readLeilaoFromSpace(space);
				try {
					space.write(leilao, null, 600 * 1000);
				} catch (RemoteException | TransactionException e1) {
					e1.printStackTrace();
				}
				formattedLeilaoItems(items);
			}
		});
		
		btnNewButton_1.setBounds(417, 1, 117, 29);
		frame.getContentPane().add(btnNewButton_1);
		offerButton.setMnemonic(KeyEvent.VK_D);
		
		
		Leilao leilao = readLeilaoFromSpace(space);
		try {
			space.write(leilao, null, 600 * 1000);
		} catch (RemoteException | TransactionException e1) {
			e1.printStackTrace();
		}
		
		formattedLeilaoItems(items);
	}
		
	private Leilao readLeilaoFromSpace(JavaSpace space) {
		Leilao leilaoTemplate = new Leilao();
		try {
			Leilao leilao = (Leilao) space.take(leilaoTemplate, null, 60 * 1000);
			items = leilao.items;
			
			return leilao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null; 
	}
	
	private String formattedLeilaoItems(ArrayList<Item> items) {
		String r = "";
		tableModel.setRowCount(0);
		
		for (Item i: items) {
			Object[] objs = {i.id, i.description, i.author, "Ver info", "Ofertar lance"};
			tableModel.addRow(objs);
			
			r = r + i.id + " " + i.author + " " + i.description + " " + "\n";
		}
		return r;
	}
	
	private void addNewItem() {
		Leilao leilao = readLeilaoFromSpace(space);
		
		Item i = new Item();
		i.author = name;
		i.description = textField.getText();
		
		if (leilao.items.isEmpty() == true) {
			i.id = 0;
		}  else {
			Item lastItem = leilao.items.get(leilao.items.size() - 1);
			i.id = lastItem.id + 1;
		}
		
		leilao.items.add(i);
		items = leilao.items;
		
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
	
	private void openInfoFromItem(Item item) {
		ItemDetail itemDetail = new ItemDetail(item, name);
		itemDetail.frame.setVisible(true);
	}
	
	private void offerToItem(Item item) {
		Leilao leilao = readLeilaoFromSpace(space);
		int itemIndex = -1;
		
		for (Item i: leilao.items) {
			if (i.id == item.id) {
				itemIndex = leilao.items.indexOf(i);
			}
		}
		
		if (itemIndex != -1) {
			Object[] options1 = { "Cancelar", "Público", "Privado" };
			
			JPanel panel = new JPanel();
	        panel.add(new JLabel("Ofereça um lance para este item: "));
	        JTextField textField = new JTextField(10);
	        panel.add(textField);
	        
	        int result = JOptionPane.showOptionDialog(null, panel, "Lance",
	                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
	                null, options1, null);
	        
	       if (textField.getText().isEmpty() == false) {
				Offer newOffer = new Offer();
				newOffer.itemId = item.id;
				newOffer.value = textField.getText();
				newOffer.isPublic = result == 2 ? "0" : "1";
				newOffer.author = name;
				
				if (item.offers == null) {
					item.offers = new ArrayList<Offer>();
					item.offers.add(newOffer);
				} else {
					item.offers.add(newOffer);
				}
				
				leilao.items.get(itemIndex).offers = item.offers;
	       }
		}
		
		try {
			space.write(leilao, null, 600 * 1000);
		} catch (RemoteException | TransactionException e1) {
			e1.printStackTrace();
		}
	}
}
