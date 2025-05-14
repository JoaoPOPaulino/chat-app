
import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServidorTCP {

    private static final int PORTA = 2343;
    private static final List<ClientHandler> clientes = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor de chat iniciado na porta TCP " + PORTA + ". Aguardando conexões...");

            while (true) {
                Socket socketCliente = servidor.accept();
                ClientHandler clientHandler = new ClientHandler(socketCliente);
                clientes.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {

        private Socket socket;
        private PrintWriter saida;
        private BufferedReader entrada;
        private String nomeUsuario;
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                saida = new PrintWriter(socket.getOutputStream(), true);
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Solicita e valida o nome
                saida.println("[SERVIDOR] Digite seu nome de usuário:");
                while (true) {
                    nomeUsuario = entrada.readLine();
                    if (nomeUsuario != null && !nomeUsuario.trim().isEmpty() && !nomeExiste(nomeUsuario)) {
                        break;
                    }
                    saida.println(nomeExiste(nomeUsuario)
                            ? "[SERVIDOR] Nome já em uso. Escolha outro:"
                            : "[SERVIDOR] Nome inválido. Tente novamente:");
                }

                saida.println("[SERVIDOR] Nome aceito. Bem-vindo ao chat!");
                broadcast("[SERVIDOR] " + nomeUsuario + " entrou no chat!", this);
                // Loop de mensagens
                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    if (mensagem.equalsIgnoreCase("/sair")) {
                        break;
                    } else if (mensagem.equalsIgnoreCase("/usuarios")) {
                        saida.println(listarUsuarios());
                    } else {
                        broadcast(formatarMensagem(nomeUsuario, mensagem), this);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro com cliente " + nomeUsuario + ": " + e.getMessage());
            } finally {
                clientes.remove(this);
                broadcast("[SERVIDOR] " + nomeUsuario + " saiu do chat!", null);
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private boolean nomeExiste(String nome) {
            return clientes.stream().anyMatch(c -> c.nomeUsuario != null && c.nomeUsuario.equalsIgnoreCase(nome));
        }

        private String listarUsuarios() {
            StringBuilder sb = new StringBuilder("[SERVIDOR] Usuários online:\n");
            clientes.forEach(c -> sb.append("- ").append(c.nomeUsuario).append("\n"));
            return sb.toString();
        }

        private String formatarMensagem(String remetente, String mensagem) {
            return "[" + LocalTime.now().format(TIME_FORMATTER) + "] " + remetente + ": " + mensagem;
        }

        private void broadcast(String mensagem, ClientHandler remetente) {
            synchronized (clientes) {
                for (ClientHandler cliente : clientes) {
                    if (cliente != remetente) {
                        cliente.saida.println(mensagem);
                    }
                }
            }
            System.out.println(mensagem); // Log no servidor
        }
    }
}
