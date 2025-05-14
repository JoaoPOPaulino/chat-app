
import java.io.*;
import java.net.*;

public class ClienteTCP {

    public static void main(String[] args) {
        final String IP_SERVIDOR = "127.0.0.1";
        final int PORTA = 2343;

        try (Socket socket = new Socket(IP_SERVIDOR, PORTA)) {
            InputStream entrada = socket.getInputStream();
            byte[] buffer = new byte[500];
            int tamanho = entrada.read(buffer);

            String mensagemRecebida = new String(buffer, 0, tamanho);
            System.out.println("Mensagem recebida: " + mensagemRecebida);
        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}
