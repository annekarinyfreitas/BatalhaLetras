package com.ppd.sockets;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board {
    private JPanel mainView;
    private JPanel board;
    private JPanel myLetters;
    private JPanel opLetters;
    private JPanel chatView;
    private JPanel auxiliaryPanel;
    private JButton myAButton;
    private JButton myBButton;
    private JButton myCButton;
    private JButton myDButton;
    private JButton myEButton;
    private JButton myGButton;
    private JButton myHButton;
    private JButton myFButton;
    private JButton myIButton;
    private JButton myJButton;
    private JButton myKButton;
    private JButton myLButton;
    private JButton myMButton;
    private JButton myOButton;
    private JButton myPButton;
    private JButton myNButton;
    private JButton myQButton;
    private JButton myRButton;
    private JButton mySButton;
    private JButton myTButton;
    private JButton myUButton;
    private JButton myVButton;
    private JButton myWButton;
    private JButton myXButton;
    private JButton myYButton;
    private JButton myZButton;
    private JButton opAButton;
    private JButton opBButton;
    private JButton opCButton;
    private JButton opDButton;
    private JButton opEButton;
    private JButton opFButton;
    private JButton opGButton;
    private JButton opHButton;
    private JButton opIButton;
    private JButton opJButton;
    private JButton opKButton;
    private JButton opLButton;
    private JButton opMButton;
    private JButton opOButton;
    private JButton opPButton;
    private JButton opNButton;
    private JButton opQButton;
    private JButton opRButton;
    private JButton opSButton;
    private JButton opTButton;
    private JButton opUButton;
    private JButton opVButton;
    private JButton opWButton;
    private JButton opXButton;
    private JButton opYButton;
    private JButton opZButton;
    JTextArea receivedText;
    JButton sendMessageButton;
    JTextField textToSend;
    private JPanel dicePanel;
    private JButton diceButton;
    private JTextArea boardLog;
    private JPanel boardLogPanel;
    private JButton finishTurnButton;
    private JButton desistirDoJogoButton;

    public Board(String playerName) {
        JFrame frame = new JFrame("Batalha das Letras "+playerName);
        frame.setContentPane(mainView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dado
        ImageIcon img = new ImageIcon("/Users/annekarinysilvafreitas/Desktop/BatalhaLetras/1.png");
        diceButton.setIcon(img);

        // Scroll do chat
        receivedText.setEditable(false);

        frame.pack();
        frame.setVisible(true);
    }
}
