🎉 Chat TCP - Conecte-se e Converse! 🎉
Bem-vindo ao nosso chat TCP simples, construído em Java! 🚀 Este projeto permite que você crie um ambiente de conversa em tempo real entre múltiplos usuários. Vamos explorar como ele funciona e como configurá-lo para começar a bater papo! 💬

🌟 Visão Geral
Este projeto é composto por dois programas principais:

ServidorTCP.java: O coração do chat, rodando na sua máquina real (Windows), que gerencia conexões e mensagens. ❤️
ClienteTCP.java: O seu portal para entrar no chat, podendo rodar na máquina real ou em uma VM (Ubuntu). 🌐

O Que Faz?

Conecta vários usuários ao mesmo tempo. 👥
Valida nomes de usuário para evitar duplicatas. ✅
Envia mensagens com carimbo de hora. ⏰
Suporta comandos legais como /sair e /usuarios. 🎮

Você já testou com sucesso o servidor na máquina real e um cliente na mesma máquina, além de outro cliente na VM — incrível! 🎊

📚 Estrutura do Código
🎛️ ServidorTCP.java
O servidor é o maestro dessa orquestra! 🎻 Ele:

Abre um ServerSocket na porta 2343 e espera por conexões. 🚪
Cria uma thread (ClientHandler) para cada cliente que entra. 🧵
Mantém uma lista sincronizada de clientes (clientes) para evitar bagunça. 📋
Checa se os nomes de usuário são únicos e válidos. 🔍
Faz o broadcast de mensagens para todos, exceto quem enviou. 📢
Anuncia quando alguém entra ou sai do chat. 🔔

Destaques:

main: Inicia o servidor e aceita conexões.
ClientHandler: Cuida de cada cliente individualmente.
nomeExiste: Verifica se um nome já está em uso.
broadcast: Espalha as mensagens para todos.
listarUsuarios: Mostra quem está online.
formatarMensagem: Adiciona o horário às mensagens.

👤 ClienteTCP.java
O cliente é sua janela para o mundo do chat! 🪟 Ele:

Conecta ao servidor no IP 192.168.15.68 e porta 2343. 🌉
Envia um nome de usuário para validação. 📝
Usa uma thread para receber mensagens do servidor. 🔄
Envia suas mensagens digitadas ao servidor. 💌
Processa comandos como /sair e /usuarios. 🎛️

Destaques:

main: Gerencia conexão, nome e envio/recebimento de mensagens.
Thread de recebimento: Exibe mensagens recebidas.
Loop de envio: Lê suas mensagens e as envia.


🛠️ Pré-requisitos
Antes de começar, certifique-se de ter:

Java: JDK 8 ou superior instalado na máquina real e na VM. Verifique com:
java -version
javac -version

✨
Rede:
IP da máquina real (ex.: 192.168.15.68) configurado. 📡
VM em modo Bridged ou NAT para se conectar à máquina real. 🌐
Teste a conexão:ping 192.168.15.68



🌐
Firewall:
No Windows, libere a porta 2343:
netsh advfirewall firewall add rule name="Chat TCP 2343" dir=in action=allow protocol=TCP localport=2343
🔒

No Ubuntu, confirme que o firewall está desativado ou libere a porta:
sudo ufw status
sudo ufw allow 2343/tcp
🔓




🚀 Instruções para Configuração e Execução

1. Prepare os Arquivos 🌱
Salve ServidorTCP.java e ClienteTCP.java na máquina real (Windows).
Copie ClienteTCP.java para a VM (Ubuntu).

2. Compile os Códigos ⚙️
Máquina Real (Windows):javac ServidorTCP.java
javac ClienteTCP.java

VM (Ubuntu):
javac ClienteTCP.java

3. Inicie o Servidor (Máquina Real - Windows) 🎬
Execute: java ServidorTCP


Saída esperada:Servidor de chat iniciado na porta TCP 2343. Aguardando conexões...
[DEBUG] Lista de clientes inicializada vazia.


4. Conecte o Primeiro Cliente (Máquina Real - Windows) 👨‍💻
Em outro terminal:
java ClienteTCP


Digite um nome (ex.: Jp) e pressione Enter.
Saída esperada:Conectado ao servidor 192.168.15.68:2343
Digite seu nome de usuário: Jp
[SERVIDOR] Nome aceito. Bem-vindo ao chat!
[SERVIDOR] Jp entrou no chat!
--- Chat Iniciado ---
Digite mensagens ou use /sair para sair, /usuarios para listar usuários.
>


5. Conecte o Segundo Cliente (VM - Ubuntu) 👩‍💻
Na VM:
java ClienteTCP


Digite um nome diferente (ex.: Ana) e pressione Enter.
Saída esperada:Conectado ao servidor 192.168.15.68:2343
Digite seu nome de usuário: Ana
[SERVIDOR] Nome aceito. Bem-vindo ao chat!
[SERVIDOR] Ana entrou no chat!
--- Chat Iniciado ---
Digite mensagens ou use /sair para sair, /usuarios para listar usuários.
>



6. Comece a Conversar! 💬

No cliente Jp (máquina real), digite: Olá, Ana! e pressione Enter.
No cliente Ana (VM), veja:[23:50:00] Jp: Olá, Ana!


Responda no cliente Ana: Oi, Jp! Tudo bem? e pressione Enter.
No cliente Jp, veja:[23:50:05] Ana: Oi, Jp! Tudo bem?


Use /usuarios para listar quem está online:[SERVIDOR] Usuários online:
- Jp
- Ana


Use /sair para sair de um cliente.


💡 Notas e Dicas

IP do Servidor: O IP 192.168.15.68 está fixo. Se for diferente, mude IP_SERVIDOR em ClienteTCP.java. 📍
Porta: Usa 2343. Se ocupada, altere para 2344 em ambos os arquivos. 🔢
Erros de Sintaxe: Você mencionou erros de sintaxe, mas optou por não mexer. Se o código não compilar, veja os erros com javac e me avise! ⚠️
Mais Clientes: Adicione outros clientes em novas janelas ou máquinas para mais diversão! 🎈


📜 Exemplo de Saída
Servidor (Windows) 📡
Servidor de chat iniciado na porta TCP 2343. Aguardando conexões...
[DEBUG] Lista de clientes inicializada vazia.
[DEBUG] Nova conexão de /127.0.0.1
[DEBUG] Verificando nome 'Jp': Disponível
[DEBUG] Nome aceito: Jp
[SERVIDOR] Jp entrou no chat!
[DEBUG] Nova conexão de /192.168.15.x
[DEBUG] Verificando nome 'Ana': Disponível
[DEBUG] Nome aceito: Ana
[SERVIDOR] Ana entrou no chat!
[DEBUG] Recebida mensagem de Jp: Olá, Ana!
[23:50:00] Jp: Olá, Ana!

Cliente na Máquina Real (Windows) 💻
Conectado ao servidor 192.168.15.68:2343
Digite seu nome de usuário: Jp
[SERVIDOR] Nome aceito. Bem-vindo ao chat!
[SERVIDOR] Jp entrou no chat!
--- Chat Iniciado ---
Digite mensagens ou use /sair para sair, /usuarios para listar usuários.
> Olá, Ana!
[DEBUG] Enviada mensagem: Olá, Ana!
[23:50:05] Ana: Oi, Jp! Tudo bem?

Cliente na VM (Ubuntu) 🖥️
Conectado ao servidor 192.168.15.68:2343
Digite seu nome de usuário: Ana
[SERVIDOR] Nome aceito. Bem-vindo ao chat!
[SERVIDOR] Ana entrou no chat!
--- Chat Iniciado ---
Digite mensagens ou use /sair para sair, /usuarios para listar usuários.
> [23:50:00] Jp: Olá, Ana!
Oi, Jp! Tudo bem?
[DEBUG] Enviada mensagem: Oi, Jp! Tudo bem?


🙌 Agradecimentos
Parabéns por configurar o chat com sucesso! 🌟 Se precisar de mais ajuda ou quiser adicionar novas funcionalidades (como emojis no chat 😄), é só pedir! 🚀
