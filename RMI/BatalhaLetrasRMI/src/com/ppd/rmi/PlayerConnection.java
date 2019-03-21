package com.ppd.rmi;

import javax.swing.*;

public class PlayerConnection {
    private JPanel mainView;
    JTextField ipTextField;
    JTextField playerNameTextField;
    JButton conectButton;
    JFrame frame;

    public PlayerConnection() {
        frame = new JFrame("Conexao do Jogador");
        frame.setContentPane(mainView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        ipTextField.setText("localhost");
    }
}
