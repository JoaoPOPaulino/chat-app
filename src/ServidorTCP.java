
import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ServidorTCP {

    private static final int PORTA = 2343;
    private static final List<ClientHandler> clientes = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor de chat iniciado na porta TCP " + PORTA + ". Aguardando conexões...");
            synchronized (clientes) {
                clientes.clear();
                System.out.println("[DEBUG] Lista de clientes inicializada vazia.");
            }

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("[DEBUG] Nova conexão de " + socketCliente.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socketCliente);
                synchronized (clientes) {
                    clientes.add(clientHandler);
                }
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
                saida.flush();
                while (true) {
                    String nome = entrada.readLine();
                    if (nome == null) {
                        System.out.println("[DEBUG] Cliente desconectou antes de enviar nome.");
                        return;
                    }
                    if (!nome.trim().isEmpty() && !nomeExiste(nome)) {
                        nomeUsuario = nome;
                        break;
                    }
                    saida.println(nomeExiste(nome)
                            ? "[SERVIDOR] Nome já em uso. Escolha outro:"
                            : "[SERVIDOR] Nome inválido. Tente novamente:");
                    saida.flush();
                }

                System.out.println("[DEBUG] Nome aceito: " + nomeUsuario);
                saida.println("[SERVIDOR] Nome aceito. Bem-vindo ao chat!");
                saida.flush();
                broadcast("[SERVIDOR] " + nomeUsuario + " entrou no chat!", this);

                // Loop de mensagens
                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    System.out.println("[DEBUG] Recebida mensagem de " + nomeUsuario + ": " + mensagem);
                    if (mensagem.equalsIgnoreCase("/sair")) {
                        System.out.println("[DEBUG] Cliente " + nomeUsuario + " solicitou sair.");
                        break;
                    } else if (mensagem.equalsIgnoreCase("/usuarios")) {
                        saida.println(listarUsuarios());
                        saida.flush();
                    } else {
                        broadcast(formatarMensagem(nomeUsuario, mensagem), this);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro com cliente " + (nomeUsuario != null ? nomeUsuario : "Desconhecido") + ": " + e.getMessage());
            } finally {
                if (nomeUsuario != null) {
                    synchronized (clientes) {
                        clientes.remove(this);
                    }
                    broadcast("[SERVIDOR] " + nomeUsuario + " saiu do chat!", null);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        private boolean nomeExiste(String nome) {
            boolean existe = clientes.stream()
                    .anyMatch(c -> c.nomeUsuario != null && c.nomeUsuario.equalsIgnoreCase(nome));
            System.out.println("[DEBUG] Verificando nome '" + nome + "': " + (existe ? "Já existe" : "Disponível"));
            if (existe) {
                System.out.println("[DEBUG] Nomes atuais: "
                        + clientes.stream()
                                .map(c -> c.nomeUsuario != null ? c.nomeUsuario : "null")
                                .collect(Collectors.joining(", ")));
            }
            return existe;
        }

        private String listarUsuarios() {
            StringBuilder sb = new StringBuilder("[SERVIDOR] Usuários online:\n");
            synchronized (clientes) {
                clientes.forEach(c -> sb.append("- ").append(c.nomeUsuario).append("\n"));
            }
            return sb.toString();
        }

        private String formatarMensagem(String remetente, String mensagem) {
            return "[" + LocalTime.now().format(TIME_FORMATTER) + "] " + remetente + ": " + mensagem;
        }

        private void broadcast(String mensagem, ClientHandler remetente) {
            System.out.println("[DEBUG] Broadcast: " + mensagem);
            synchronized (clientes) {
                for (ClientHandler cliente : clientes) {
                    if (cliente != remetente && cliente.saida != null) {
                        cliente.saida.println(mensagem);
                        cliente.saida.flush();
                    }
                }
            }
            System.out.println(mensagem);
        }
    }
}
