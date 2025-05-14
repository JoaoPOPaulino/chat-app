
import java.io.*;
import java.net.*;

public class ServidorTCP {

    public static void main(String[] args) {
        final int PORTA = 2343;

        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor escutando na porta TCP " + PORTA);

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Uma conexão do endereço " + socketCliente.getInetAddress().getHostAddress() + " foi estabelecida - enviando boas-vindas!");

                OutputStream saida = socketCliente.getOutputStream();
                String mensagem = "Esta eh uma mensagem do servidor!\n";
                saida.write(mensagem.getBytes());

                socketCliente.close();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}
