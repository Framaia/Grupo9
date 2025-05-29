package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.Gdx;  // Importa a classe Gdx da libGDX para acesso a funções de tempo e de input
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa SpriteBatch para renderizar elementos gráficos
import java.util.HashMap;  // Importa HashMap para armazenar as salas do jogo
import java.util.Map;  // Importa a interface Map usada para declarar a coleção de salas
import java.util.Random;  // Importa a classe Random para gerar números aleatórios
import dungeon.core.ai.BasicFollowerStrategy;  // Importa a estratégia básica de seguimento
import dungeon.core.events.*;  // Importa o sistema de eventos
import dungeon.core.factories.*;  // Importa as factories de criação de salas
import dungeon.core.repositories.*;  // Importa o sistema de repositórios



public class DungeonManager {
    // Instância única (padrão Singleton)
    private static DungeonManager instance;  // Variável estática que guarda a única instância da classe

    private RoomRepository roomRepository;  // Repositório que armazena e gere todas as salas do jogo
    private int currentRoomId;  // ID da sala atual onde o jogador se encontra

        private Player player;  // Personagem principal controlado pelo utilizador

   private Random random;  // Gera números aleatórios para criar variação no comportamento dos inimigos e na distribuição de itens

    // Sistema de eventos usando Observer Pattern
    private GameEventManager eventManager;  // Gestor central de todos os eventos do jogo
    private GameLogger gameLogger;  // Observador que faz log dos eventos para debug
    private ScoreManager scoreManager;  // Observador que mantém as estatísticas do jogador

    // Largura e altura da janela do jogo utilizadas para calcular as coordenadas do jogador, dos inimigos, das portas e dos itens
    private static final int SCREEN_WIDTH = 800;  // Largura da janela do jogo em pixels
    private static final int SCREEN_HEIGHT = 600;  // Altura da janela do jogo em pixels


    private DungeonManager() {  // Construtor para implementar o padrão Singleton
        roomRepository = new InMemoryRoomRepository();  // Inicializa o repositório de salas
        random = new Random();  // Cria um novo gerador de números aleatórios

        player = new Player((SCREEN_WIDTH - 128) / 2, (SCREEN_HEIGHT - 128) / 2);  // Cria o jogador no centro do ecrã. 128 é a largura/altura do jogador.

        // Inicializa o sistema de eventos
        eventManager = GameEventManager.getInstance();  // Obtém a instância única do gestor de eventos
        gameLogger = new GameLogger(true);  // Cria o logger com modo debug ativo
        scoreManager = new ScoreManager();  // Cria o gestor de pontuações

        // Regista os observadores no sistema de eventos
        eventManager.addObserver(gameLogger);  // Adiciona o logger como observador
        eventManager.addObserver(scoreManager);  // Adiciona o score manager como observador

        generateDungeon();  // Chama o método que cria todas as salas e ligações entre elas
    }

    public static DungeonManager getInstance() {  // Método que permite aceder ao DungeonManager a partir de qualquer classe do jogo
		if (instance == null) {  // Verifica se esta é a primeira vez que o método é chamado
            instance = new DungeonManager();  // Inicializa o jogo pela primeira vez
		}
		return instance;  // Devolve a referência ao objeto que coordena o jogo
    }

    /*
      Gera o jogo completo com várias salas interligadas.
      Configura os inimigos, os itens e as portas em cada sala.
      Atualmente cria um layout básico com 4 salas, mas pode ser expandido para usar algoritmos mais complexos.
     */
    private void generateDungeon() {  // Método que cria todas as salas usando Factory Pattern
        // Factory para sala inicial - fácil para iniciantes
        RoomFactory startingFactory = new StartingRoomFactory();
        Room startingRoom = startingFactory.createRoom(0);
        roomRepository.saveRoom(startingRoom); // Armazena sala inicial no repositório

        // Factory para sala de combate norte (ID 1)
        RoomFactory northRoomFactory = new CombatRoomFactory(1);
        Room northRoom = northRoomFactory.createRoom(1);
        roomRepository.saveRoom(northRoom); // Armazena sala norte no repositório

        // Factory para sala de combate leste (ID 2)
        RoomFactory eastRoomFactory = new CombatRoomFactory(2);
        Room eastRoom = eastRoomFactory.createRoom(2);
        roomRepository.saveRoom(eastRoom); // Armazena sala leste no repositório

        // Factory para sala do boss (ID 3)
        RoomFactory bossFactory = new BossRoomFactory();
        Room bossRoom = bossFactory.createRoom(3);
        roomRepository.saveRoom(bossRoom); // Armazena sala do boss no repositório

        // Define a sala inicial para o jogador começar
        currentRoomId = 0;  // O jogador começa na sala com ID 0

        System.out.println("Dungeon gerado com Factory e Repository Pattern! " + roomRepository.getRoomCount() + " salas criadas.");
    }


    public void update(float deltaTime) {  // Método chamado a cada frame para atualizar o estado do jogo
        // Atualiza a posição e o estado do jogador
        player.update(deltaTime);  // Atualiza a posição e estado do jogador com base nas teclas pressionadas

        // Atualiza a sala atual e tudo o que está dentro dela
        Room currentRoom = roomRepository.findRoomById(currentRoomId);  // Obtém a sala atual do repositório
        currentRoom.update(deltaTime, player);  // Atualiza a sala atual, incluindo inimigos e itens

        // Verifica se o jogador está a tentar mudar de sala
        if (player.isInRoomTransition()) {  // Verifica se o jogador está a interagir com uma porta para mudar de sala
            handleRoomTransition();  // Processa a mudança de sala
        }
    }


    public void render(SpriteBatch batch) {  // Método chamado a cada frame para desenhar o jogo
        // Desenha a sala atual
        roomRepository.findRoomById(currentRoomId).render(batch);  // Desenha a sala atual obtida do repositório

        // Desenha o jogador
        player.render(batch);  // Desenha o jogador por cima da sala
    }

    /*
      Processa a transição do jogador entre salas diferentes.
      Quando o jogador usa uma porta, este método é chamado para mudar para a nova sala e reposicionar o jogador adequadamente.
     */
    private void handleRoomTransition() {  // Método que gerencia a mudança de sala quando o jogador usa uma porta
        Room.DoorPosition exitDirection = player.getExitDirection();  // Obtém a direção da saída (porta usada)
        Room currentRoom = roomRepository.findRoomById(currentRoomId);  // Obtém a sala atual do repositório
        Door exitDoor = currentRoom.getDoor(exitDirection);  // Obtém a porta de saída usada pelo jogador

        if (exitDoor != null && !exitDoor.isLocked()) {  // Verifica se a porta existe e não está trancada
            int nextRoomId = exitDoor.getTargetRoomId();  // Obtém o ID da sala de destino
            Room nextRoom = roomRepository.findRoomById(nextRoomId);  // Obtém a próxima sala do repositório

            if (nextRoom != null) {  // Verifica se a sala de destino existe
                // Determina a posição de entrada na nova sala
                Room.DoorPosition entryDirection = exitDirection.getOpposite();  // Calcula a direção oposta para a entrada
                Door entryDoor = nextRoom.getDoor(entryDirection);  // Obtém a porta de entrada na nova sala

                // Reposiciona o jogador junto à porta de entrada
                float newX = 0;  // Inicializa a nova coordenada X
                float newY = 0;  // Inicializa a nova coordenada Y

                switch (entryDirection) {  // Determina a posição exata com base na direção de entrada
                    case NORTH:  // Quando a entrada é pela porta norte
                        newX = SCREEN_WIDTH / 2 - player.getWidth() / 2;  // Alinha o jogador horizontalmente com o centro da sala
                        newY = SCREEN_HEIGHT - player.getHeight() - 20;  // Posiciona perto do topo da sala
                        break;
                    case SOUTH:  // Quando a entrada é pela porta sul
                        newX = SCREEN_WIDTH / 2 - player.getWidth() / 2;  // Alinha o jogador horizontalmente com o centro da sala
                        newY = 20;  // Posiciona perto da parte inferior da sala
                        break;
                    case EAST:  // Quando a entrada é pela porta leste
                        newX = SCREEN_WIDTH - player.getWidth() - 20;  // Posiciona perto da parede direita
                        newY = SCREEN_HEIGHT / 2 - player.getHeight() / 2;  // Alinha o jogador verticalmente com o centro da sala
                        break;
                    case WEST:  // Quando a entrada é pela porta oeste
                        newX = 20;  // Posiciona perto da parede esquerda
                        newY = SCREEN_HEIGHT / 2 - player.getHeight() / 2;  // Alinha o jogador verticalmente com o centro da sala
                        break;
                }

                player.setPosition(newX, newY);  // Define a nova posição do jogador na sala de destino

                // Atualiza o ID da sala atual
                currentRoomId = nextRoomId;  // Muda a sala atual para a sala de destino
                           }
        }

        // Limpa a flag de transição do jogador
        player.resetRoomTransition();  // Reinicia o estado de transição para que o jogador possa usar outras portas depois
    }

    public ScoreManager getScoreManager() {  // Permite acesso às estatísticas do jogo
        return scoreManager;  // Retorna referência ao gestor de pontuações
    }

    public GameEventManager getEventManager() {  // Permite acesso ao sistema de eventos
        return eventManager;  // Retorna referência ao gestor de eventos
    }

    public void dispose() {  // Método para libertar os recursos quando o jogo termina
        for (Room room : roomRepository.findAllRooms()) {  // Percorre todas as salas do repositório
            room.dispose();  // Liberta os recursos de cada sala
        }
        player.dispose();  // Liberta os recursos do jogador
    }

    public Player getPlayer() {  // Método que permite aceder ao personagem do jogador a partir de outras classes
		return player;  // Devolve a referência ao personagem controlado pelo utilizador
	}

    public Room getCurrentRoom() {  // Método que fornece acesso à sala atual
        return roomRepository.findRoomById(currentRoomId);  // Devolve a sala atual do repositório
    }

    public int getCurrentRoomId() {  // Método que devolve o ID da sala atual
        return currentRoomId;  // Devolve o ID numérico da sala atual
    }
}
