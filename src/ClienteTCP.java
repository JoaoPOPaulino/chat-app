
import java.io.*;
import java.net.*;

public class ClienteTCP {

    private static final String IP_SERVIDOR = "192.168.15.68"; // Ajuste para o IP da sua máquina real
    private static final int PORTA = 2343;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA)) {
            System.out.println("Conectado ao servidor " + IP_SERVIDOR + ":" + PORTA);

            // Configura streams
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread para receber mensagens
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println("Recebido: " + mensagem);
                    }
                    System.out.println("Conexão com servidor encerrada.");
                } catch (IOException e) {
                    System.out.println("Erro ao receber mensagens: " + e.getMessage());
                }
            }).start();

            // Recebe solicitação de nome
            String prompt = entrada.readLine();
            System.out.println("Servidor: " + prompt);
            System.out.print("Nome de usuário: ");
            String nomeUsuario = console.readLine();
            saida.println(nomeUsuario);
            saida.flush(); // Garante que o nome é enviado

            // Loop para enviar mensagens
            System.out.println("Digite suas mensagens (ou '/sair' para encerrar):");
            String mensagem;
            while ((mensagem = console.readLine()) != null) {
                saida.println(mensagem);
                saida.flush(); // Garante que a mensagem é enviada
                System.out.println("Enviado: " + mensagem);
                if (mensagem.equalsIgnoreCase("/sair")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
        System.out.println("Cliente encerrado.");
    }
}
