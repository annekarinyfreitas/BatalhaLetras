package com.ppd.sockets;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Board {
    private JPanel mainView;
    private JPanel board;
    private JPanel myLetters;
    private JPanel opLetters;
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
    private JPanel dicePanel;
    private JPanel boardLogPanel;
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
    JPanel chatView;

    JTextField selectedWord;
    JButton restartPlayButton;
    JTextArea receivedText;
    JButton sendMessageButton;
    JTextField textToSend;
    JButton diceButton;
    JTextArea boardLog;
    JButton sendGameWordButton;
    JButton giveUpGameButton;

    JLabel[] boardLetters;
    JButton[] myGameLetters;
    JButton[] oponentsGameLetters;

    JFrame frame;
    private String[] alphabetLetters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

//    public static void main(String[] args) {
//        new Board("Teste");
//    }

    public Board(String playerName) {
        frame = new JFrame("Batalha das Letras "+playerName);
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

        // Scroll do chat
        DefaultCaret caret = (DefaultCaret)receivedText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Scroll do log
        DefaultCaret logCaret = (DefaultCaret)boardLog.getCaret();
        logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        frame.pack();
        frame.setVisible(true);
    }

    // RESETA AS LETRAS DO TABULEIRO, PARA REMOVER A POSICAO DOS JOGADORES
    public void resetLetters() {
        for (JLabel label: boardLetters) {
            label.setText(" ");
        }
    }

    // ATUALIZA AS LETRAS DOS JOGADORES
    public void updateGameLetters(List <String> letters, JButton[] buttons) {
        if (!letters.isEmpty()) {
            for (JButton button: buttons) {
                if (!letters.contains(button.getText())) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        }
    }

    private JLabel setLetterImageToLabel(String letter) {
        JLabel label = new JLabel();
        label.setName(letter);

        label.setForeground(Color.white);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);

        if (letter.equals("I")) {
            label.setSize(20, 45);
        } else {
            label.setSize(35, 45);
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("images/letters/" + letter + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        label.setIcon(imageIcon);

        return label;
    }

    private void createUIComponents() {
        aLabel = setLetterImageToLabel("A");
        bLabel = setLetterImageToLabel("B");
        cLabel = setLetterImageToLabel("C");
        dLabel = setLetterImageToLabel("D");
        eLabel = setLetterImageToLabel("E");
        fLabel = setLetterImageToLabel("F");
        gLabel = setLetterImageToLabel("G");
        hLabel = setLetterImageToLabel("H");
        iLabel = setLetterImageToLabel("I");
        jLabel = setLetterImageToLabel("J");
        kLabel = setLetterImageToLabel("K");
        lLabel = setLetterImageToLabel("L");
        mLabel = setLetterImageToLabel("M");
        nLabel = setLetterImageToLabel("N");
        oLabel = setLetterImageToLabel("O");
        pLabel = setLetterImageToLabel("P");
        qLabel = setLetterImageToLabel("Q");
        rLabel = setLetterImageToLabel("R");
        sLabel = setLetterImageToLabel("S");
        tLabel = setLetterImageToLabel("T");
        uLabel = setLetterImageToLabel("U");
        vLabel = setLetterImageToLabel("V");
        wLabel = setLetterImageToLabel("W");
        xLabel = setLetterImageToLabel("X");
        yLabel = setLetterImageToLabel("Y");
        zLabel = setLetterImageToLabel("Z");

//        boardLetters = new JLabel[]{aLabel, bLabel, cLabel, dLabel, eLabel, fLabel, gLabel, hLabel, iLabel, jLabel, kLabel, lLabel, mLabel, nLabel, oLabel, pLabel, qLabel, rLabel, sLabel, tLabel, uLabel, vLabel, wLabel, xLabel, yLabel, zLabel};
//
//        for (int i = 0; i < 26; i++) {
//            boardLetters[i] = setLetterImageToLabel(alphabetLetters[i]);
//            System.out.println("i:" + i + boardLetters[i].getName() + "letra" + alphabetLetters[i]);
//        }
    }
}
