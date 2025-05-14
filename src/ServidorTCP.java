
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorTCP {

    private static final int PORTA = 2343;
    private static final List<ClientHandler> clientes = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor de chat iniciado na porta TCP " + PORTA);

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Nova conexão de: " + socketCliente.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(socketCliente);
                clientes.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {

        private Socket socket;
        private PrintWriter saida;
        private BufferedReader entrada;
        private String nomeUsuario;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Configura streams com flush automático
                saida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Solicita o nome do usuário
                System.out.println("Enviando solicitação de nome para " + socket.getInetAddress());
                saida.println("Digite seu nome de usuário:");
                saida.flush(); // Garante que a mensagem é enviada
                nomeUsuario = entrada.readLine();
                System.out.println("Nome recebido: " + nomeUsuario);

                if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
                    nomeUsuario = "Anônimo_" + socket.getInetAddress().getHostAddress();
                }

                broadcast(nomeUsuario + " entrou no chat!");

                // Loop para receber mensagens
                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    System.out.println("Mensagem recebida de " + nomeUsuario + ": " + mensagem);
                    if (mensagem.equalsIgnoreCase("/sair")) {
                        break;
                    }
                    broadcast(nomeUsuario + ": " + mensagem);
                }
            } catch (IOException e) {
                System.out.println("Erro com cliente " + nomeUsuario + ": " + e.getMessage());
            } finally {
                clientes.remove(this);
                broadcast(nomeUsuario + " saiu do chat!");
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        private void broadcast(String mensagem) {
            synchronized (clientes) {
                for (ClientHandler cliente : clientes) {
                    cliente.saida.println(mensagem);
                    cliente.saida.flush(); // Garante que a mensagem é enviada
                }
            }
            System.out.println("Broadcast: " + mensagem);
        }
    }
}
