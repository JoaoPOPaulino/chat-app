
import java.io.*;
import java.net.*;

public class ClienteTCP {

    private static final String IP_SERVIDOR = "192.168.15.68";
    private static final int PORTA = 2343;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(IP_SERVIDOR, PORTA);
            System.out.println("Conectado ao servidor " + IP_SERVIDOR + ":" + PORTA);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread para receber mensagens do servidor
            Thread threadRecebimento = new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println(mensagem);
                    }
                } catch (IOException e) {
                    System.err.println("[ERRO] Conexão com o servidor perdida.");
                } finally {
                    System.exit(0); // Encerra o programa se a conexão cair
                }
            });
            threadRecebimento.start();

            // Solicitação do nome de usuário
            System.out.print("Digite seu nome de usuário: ");
            String nomeUsuario = console.readLine();
            saida.println(nomeUsuario); // Envia o nome para o servidor

            System.out.println("\n--- Chat Iniciado ---");
            System.out.println("Digite mensagens ou use /sair para sair.");

            // Loop principal para enviar mensagens
            String mensagem;
            while (true) {
                mensagem = console.readLine();
                if (mensagem == null || mensagem.equalsIgnoreCase("/sair")) {
                    saida.println("/sair"); // Notifica o servidor
                    break;
                }
                saida.println(mensagem);
            }

            // Encerramento
            socket.close();
            System.out.println("Conexão encerrada.");
            System.exit(0);

        } catch (UnknownHostException e) {
            System.err.println("[ERRO] Servidor não encontrado. Verifique o IP e a conexão.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }
}
