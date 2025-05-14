
import java.io.*;
import java.net.*;

public class ClienteTCP {

    private static final String IP_SERVIDOR = "192.168.15.68";
    private static final int PORTA = 2343;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP_SERVIDOR, PORTA)) {
            System.out.println("Conectado ao servidor " + IP_SERVIDOR + ":" + PORTA);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread para receber mensagens
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println(mensagem);
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao receber mensagens: " + e.getMessage());
                }
            }).start();

            // Loop de envio
            String mensagem;
            while ((mensagem = console.readLine()) != null) {
                saida.println(mensagem);
                if (mensagem.equalsIgnoreCase("/sair")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
        System.out.println("Conexão encerrada.");
    }
}
