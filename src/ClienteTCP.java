
import java.io.*;
import java.net.*;

public class ClienteTCP {

    private static final String IP_SERVIDOR = "192.168.15.68";
    private static final int PORTA = 2343;

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(IP_SERVIDOR, PORTA);
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
                    System.out.println("[DEBUG] Fim da thread de recebimento.");
                } catch (IOException e) {
                    System.err.println("[ERRO] Conexão com o servidor perdida: " + e.getMessage());
                }
            });
            threadRecebimento.start();

            // Solicitação do nome de usuário
            String nomeUsuario;
            String mensagem;
            while (true) {
                System.out.print("Digite seu nome de usuário: ");
                nomeUsuario = console.readLine();
                if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
                    System.out.println("Nome inválido. Tente novamente.");
                    continue;
                }
                saida.println(nomeUsuario); // Envia o nome
                mensagem = entrada.readLine(); // Recebe resposta
                System.out.println(mensagem);
                if (mensagem.contains("Nome aceito")) {
                    break;
                }
            }

            System.out.println("\n--- Chat Iniciado ---");
            System.out.println("Digite mensagens ou use /sair para sair, /usuarios para listar usuários.");

            // Loop principal para enviar mensagens
            String mensagemUsuario;
            while (true) {
                System.out.print("> "); // Prompt para o usuário
                mensagemUsuario = console.readLine();
                if (mensagemUsuario == null) {
                    System.out.println("[DEBUG] Entrada nula, encerrando cliente.");
                    break;
                }
                mensagemUsuario = mensagemUsuario.trim();
                if (mensagemUsuario.equalsIgnoreCase("/sair")) {
                    saida.println("/sair");
                    System.out.println("[DEBUG] Enviado /sair ao servidor.");
                    break;
                } else if (mensagemUsuario.equalsIgnoreCase("/usuarios")) {
                    saida.println("/usuarios");
                    System.out.println("[DEBUG] Solicitado lista de usuários.");
                } else if (!mensagemUsuario.isEmpty()) {
                    saida.println(mensagemUsuario);
                    System.out.println("[DEBUG] Enviada mensagem: " + mensagemUsuario);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("[ERRO] Servidor não encontrado. Verifique o IP e a conexão.");
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível conectar ao servidor: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    System.out.println("Conexão encerrada.");
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }
    }
}
