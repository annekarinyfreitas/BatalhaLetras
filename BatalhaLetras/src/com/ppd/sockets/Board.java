package com.ppd.sockets;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private JPanel mainView;
    private JPanel board;
    private JPanel myLetters;
    private JPanel opLetters;
    JPanel chatView;
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
    JButton diceButton;
    JTextArea boardLog;
    private JPanel boardLogPanel;
    JButton sendGameWordButton;
    private JButton giveUpGameButton;
    private JLabel aLabel;
    private JLabel bLabel;
    private JLabel cLabel;
    private JLabel dLabel;
    private JLabel eLabel;
    private JLabel fLabel;
    private JLabel gLabel;
    private JLabel hLabel;
    private JLabel iLabel;
    private JLabel jLabel;
    private JLabel kLabel;
    private JLabel lLabel;
    private JLabel mLabel;
    private JLabel nLabel;
    private JLabel oLabel;
    private JLabel pLabel;
    private JLabel qLabel;
    private JLabel rLabel;
    private JLabel sLabel;
    private JLabel tLabel;
    private JLabel uLabel;
    private JLabel vLabel;
    private JLabel wLabel;
    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel zLabel;
    JTextField selectedWord;
    JButton passTurnButton;

    JLabel[] boardLetters;
    JButton[] myGameLetters;
    JButton[] oponentsGameLetters;

    public Board(String playerName) {
        JFrame frame = new JFrame("Batalha das Letras "+playerName);
        frame.setContentPane(mainView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dado
        ImageIcon img = new ImageIcon("/Users/annekarinysilvafreitas/Desktop/BatalhaLetras/1.png");
        diceButton.setIcon(img);

        // Chat
        boardLog.setEditable(false);
        receivedText.setEditable(false);

        // Letras do tabuleiro principal
        boardLetters = new JLabel[]{aLabel, bLabel, cLabel, dLabel, eLabel, fLabel, gLabel, hLabel, iLabel, jLabel, kLabel, lLabel, mLabel, nLabel, oLabel, pLabel, qLabel, rLabel, sLabel, tLabel, uLabel, vLabel, wLabel, xLabel, yLabel, zLabel};
        myGameLetters = new JButton[]{myAButton, myBButton, myCButton, myDButton, myEButton, myFButton, myGButton, myHButton, myIButton, myJButton, myKButton, myLButton, myMButton, myNButton, myOButton, myPButton, myQButton, myRButton, mySButton, myTButton, myUButton, myVButton, myWButton, myXButton, myYButton, myZButton};
        oponentsGameLetters = new JButton[]{opAButton, opBButton, opCButton, opDButton, opEButton, opFButton, opGButton, opHButton, opIButton, opJButton, opKButton, opLButton, opMButton, opNButton, opOButton, opPButton, opQButton, opRButton, opSButton, opTButton, opUButton, opVButton, opWButton, opXButton, opYButton, opZButton};


        frame.pack();
        frame.setVisible(true);
    }

    public void resetLetters() {
        aLabel.setText("A");
        bLabel.setText("B");
        cLabel.setText("C");
        dLabel.setText("D");
        eLabel.setText("E");
        fLabel.setText("F");
        gLabel.setText("G");
        hLabel.setText("H");
        iLabel.setText("I");
        jLabel.setText("J");
        kLabel.setText("K");
        lLabel.setText("L");
        mLabel.setText("M");
        nLabel.setText("N");
        oLabel.setText("O");
        pLabel.setText("P");
        qLabel.setText("Q");
        rLabel.setText("R");
        sLabel.setText("S");
        tLabel.setText("T");
        uLabel.setText("U");
        vLabel.setText("V");
        wLabel.setText("W");
        xLabel.setText("X");
        wLabel.setText("W");
        yLabel.setText("Y");
        zLabel.setText("Z");
    }

    public void updateMyLetters(List <String> myLetters) {
        for (JButton button: myGameLetters) {
            if (!myLetters.contains(button.getText())) {
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    }

    public void updateOponentsLetters(List <String> opLetters) {
        for (JButton button: oponentsGameLetters) {
            if (!opLetters.contains(button.getText())) {
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    }
}
