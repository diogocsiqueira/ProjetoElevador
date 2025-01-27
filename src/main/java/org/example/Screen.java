package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Screen extends JFrame {

    private boolean[] andaresChamados = new boolean[4];
    private int andarAtual = 1;
    private List<Integer> fila = new ArrayList<>();
    private JButton[] botaoAndarAtual = new JButton[4];
    JButton[] botaoPainelDeControle = new JButton[4];

    public Screen() {
        setTitle("Elevador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        // Painel de controle
        JPanel painelControle = new JPanel(new GridLayout(2, 2, 10, 10));
        painelControle.setBorder(BorderFactory.createTitledBorder("Painel de Controle"));

        for (int i = 0; i < 4; i++) {
            botaoPainelDeControle[i] = new JButton("Andar: " + (i + 1));
            botaoPainelDeControle[i].setBorder(new LineBorder(Color.RED, 3));
            painelControle.add(botaoPainelDeControle[i]);
            int andar = i + 1;
            botaoPainelDeControle[i].addActionListener(actionEvent -> chamarParaAndar(actionEvent, andar));
        }
        add(painelControle, BorderLayout.WEST);

        // Painel do poço do elevador
        JPanel pocoElevador = new JPanel(null);
        pocoElevador.setBorder(BorderFactory.createTitledBorder("Poço Elevador"));

        // Botões representando os andares e o andar onde o elevador esta
        for (int i = 0; i < 4; i++) {
            botaoAndarAtual[i] = new JButton("Andar " + (i + 1));
            botaoAndarAtual[i].setBounds(10, 400 - (i * 100), 100, 50);
            botaoAndarAtual[i].setEnabled(false);
            pocoElevador.add(botaoAndarAtual[i]);
        }
        botaoAndarAtual[0].setEnabled(true);

        add(pocoElevador, BorderLayout.CENTER);

        // Painel dos botões de subir e descer
        JPanel painelAndares = new JPanel();
        painelAndares.setLayout(new GridLayout(4, 1, 5, 5));
        painelAndares.setBorder(BorderFactory.createTitledBorder("Andares"));
        JButton[] botaoSubir = new JButton[4];
        JButton[] botaoDescer = new JButton[4];
        for (int i = 0; i < 4; i++) {
            JPanel andarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            JLabel andarLabel = new JLabel("Andar " + (4 - i));
            botaoSubir[i] = new JButton("Subir");
            botaoDescer[i] = new JButton("Descer");

            if (i == 0) botaoSubir[i].setEnabled(false);
            if (i == 3) botaoDescer[i].setEnabled(false);

            int andar = 4 - i;
            botaoSubir[i].addActionListener(actionEvent -> chamarParaAndar(actionEvent, andar));
            botaoDescer[i].addActionListener(actionEvent -> chamarParaAndar(actionEvent, andar));

            andarPanel.add(andarLabel);
            andarPanel.add(botaoSubir[i]);
            andarPanel.add(botaoDescer[i]);
            painelAndares.add(andarPanel);
        }
        add(painelAndares, BorderLayout.EAST);

        iniciarMovimentoElevador();

        setVisible(true);
    }

    //adiciona os processos na fila que o elevador irá seguir,
    //trocando o botão dos andares q ja estão clicados
    public void chamarParaAndar(ActionEvent actionEvent, int botao) {
        if (!andaresChamados[botao - 1]) {
            botaoPainelDeControle[botao - 1].setBorder(new LineBorder(Color.GREEN, 3));
            fila.add(botao);
        }
    }

    //Faz um timer e verifica se tem algo na fila, se tiver ele move o elevador
    // seguindo a ordem da fila, caso n tenha o elevador volta pro primeiro andar
    //a ordem da fila é decidida pela ordem em q é chamado
    private void iniciarMovimentoElevador() {
        Thread movimentoThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (!fila.isEmpty()) {
                        funcaoMover(fila.getFirst());
                        fila.removeFirst();
                    } else {
                        funcaoMover(1);
                    }
                } catch (InterruptedException e) {}
            }
        });
        movimentoThread.start();
    }

    //Move o elevador pelo poço até o andar destino desligando
    //e ligando os botões que representam a posição dele, alem de fazer o
    //processo de trocar a cor da borda do boão e chama a função q faz som quando chega no andar
    public void funcaoMover(int andarDestino) {
        if (andarDestino > andarAtual) {
            for (int i = andarAtual; i < andarDestino; i++) {
                botaoAndarAtual[andarAtual - 1].setEnabled(false);
                andarAtual += 1;
                botaoAndarAtual[andarAtual - 1].setEnabled(true);
                if(andarDestino == andarAtual){
                    tocarBeep();
                    andaresChamados[andarAtual - 1] = false;
                    botaoPainelDeControle[andarAtual - 1].setBorder(new LineBorder(Color.RED, 3));
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
            }
            andaresChamados[andarAtual - 1] = false;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        } else {
            for (int i = andarAtual; i > andarDestino; i--) {
                botaoAndarAtual[andarAtual - 1].setEnabled(false);
                andarAtual -= 1;
                botaoAndarAtual[andarAtual - 1].setEnabled(true);
                if(andarDestino == andarAtual){
                    tocarBeep();
                    andaresChamados[andarAtual - 1] = false;
                    botaoPainelDeControle[andarAtual - 1].setBorder(new LineBorder(Color.RED, 3));
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }

    //Faz um som quando chamado
    public static void tocarBeep() {
        Toolkit.getDefaultToolkit().beep();
    }
}
