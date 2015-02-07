Truco dos Amigos
=====
<p>
<strong><span style="1.2em">Truco dos Amigos</span> funciona multiplayer e pode ser jogado SIMPLES (2 jogadores), DUPLA (4 jogadores) ou DOURADINHA (6 jogadores). Cliente e servidor inclusos.</strong>
<p>

<h2>Executando o Servidor</h2>
<pre>java -cp truco.jar miquilini.felipe.servidor.Servidor</pre>
<p>Será requerido a porta que deseja rodar o server (5000 por exemplo),
a senha para conectar e a modalidade. Quando tudo estiver certo você receberá a mensagem "Servidor rodando na porta 5000"</p>

<h2>Executando o Cliente</h2>
<p>Para executar o cliente basta dar 2 cliques no arquivo ou usar o comando:</p>
<pre>java -cp truco.jar miquilini.felipe.clienteSwing.ClienteSwing</pre>
<p>Pedirá para inserir o ip, a porta, a senha e uma identificação.</p>
<p>Logo você verá o lobby, onde é apresentados os jogadores que estão
logados.</p>
<p><strong>Jogando: </strong>este jogo flui com os comandos que os 
jogadores inserem no canal de chat (os comandos começam com o símbolo
de "menor que" e terminam com o símbolo de "maior que"), para ver os 
comandos digite o comando de ajuda 
<strong>/help</strong>, logo então na parte superior será 
apresentado a relação dos comandos. Você perceberá que eles são
bem diretos e simples</p>

<p><em>truco.jar</em> está dentro do diretório <em>lib</em></p>

<h3>Dica</h3>
<p>Para não precisar usar esses comandos java para iniciar o cliente e o servidor apenas crie um script (sh, bash, ect) nos sistemas baseados em Unix ou crie um arquivo batch no Windows</p>
