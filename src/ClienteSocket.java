import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteSocket extends JFrame implements ActionListener {

    // parte que controla a recepção de mensagens do cliente
    private Socket conexao;
    Socket socket;
    JTextArea areaTexto;
    JTextField msg;
    JButton btn;
    JButton btnSair;
    JLabel rotulo;
    PrintStream saida;
    BufferedReader entrada;

    public ClienteSocket() {
        super("Chat");
        this.setLocation(500, 300);

        Container tela = getContentPane();

        BorderLayout layout = new BorderLayout();
        tela.setLayout(layout);

        rotulo = new JLabel("");
        btn = new JButton("Enviar");
        btn.addActionListener(this);
        btnSair = new JButton("Sair");
        btnSair.addActionListener(this);
        msg = new JTextField(20);

        areaTexto = new JTextArea(15, 30);
        areaTexto.setEditable(false);
        tela.setEnabled(false);
        JScrollPane painelRolagem = new JScrollPane(areaTexto);
        painelRolagem.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        painelRolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel pSuperior = new JPanel();
        pSuperior.setLayout(new FlowLayout(FlowLayout.LEFT));
        pSuperior.add(rotulo);

        //Area quesera exibido as mensagens
        JPanel pCentro = new JPanel();
        pCentro.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pCentro.add(painelRolagem);

        JPanel pInferior = new JPanel();
        pInferior.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pInferior.add(msg);
        pInferior.add(btn);
        pInferior.add(btnSair);

        tela.add(BorderLayout.NORTH, pSuperior);
        tela.add(BorderLayout.CENTER, pCentro);
        tela.add(BorderLayout.SOUTH, pInferior);
        //setResizable(false);
        //Acionar botão com enter
        getRootPane().setDefaultButton(btn);
        pack();
        setVisible(true);

    }

    void executa() {
        try {
            socket = new Socket("127.0.0.1", 5555);
            //Instancia do atributo saida, obtem os objetos que permitem controlar o fluxo de comunicação
            saida = new PrintStream(socket.getOutputStream());
            String meuNome = JOptionPane.showInputDialog("Digite seu nome ");
            System.out.println(meuNome);
            rotulo.setText("Bem Vindo " + meuNome);
            if (meuNome == null) {
                System.exit(0);
            }
            //envia o nome digitado para o servidor
            saida.println(meuNome.toUpperCase());
            saida.println("Entrei no chat");
            //instancia a thread para ip e porta conectados
            Recebedor r = new Recebedor(socket.getInputStream());
            //Inicia a thread
            new Thread(r).start();
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public static void main(String[] args) {
        //Instancia a interface
        ClienteSocket cliente = new ClienteSocket();
        WindowListener ls = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        cliente.addWindowListener(ls);
        cliente.executa();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object fonte = event.getSource();
        if (fonte == btn) //Envia informacao pelo socket  
        {
            String text = msg.getText();
            saida.println(text);
            areaTexto.append("Você >> " + text + "\n");
            msg.setText(new String("")); //recebe do servidor  
        }
        if (fonte == btnSair) {
            saida.println("12qw78asç");//Menságem enviada onde indica que o cara saiu do batpapo
            System.exit(0);
        }
    }

    class Recebedor implements Runnable {

        private InputStream servidor;

        public Recebedor(InputStream servidor) {
            this.servidor = servidor;
        }

        @Override
        public void run() {
            try {
                //recebe mensagens de outro cliente através do servidor
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //cria variavel de mensagem
                String msg;

                while (true) {
                    // pega o que o servidor enviou
                    msg = entrada.readLine();
                    //se a mensagem contiver dados, passa pelo if, caso contrario cai no break e encerra a conexao
                    if (msg == null) {
                        System.out.println("Conexão encerrada!");
                        System.exit(0);
                    }
                    areaTexto.append(msg + "\n");
                }
            } catch (IOException e) {
                // caso ocorra alguma exceção de E/S, mostra qual foi.
                System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
            }
        }
    }
}
