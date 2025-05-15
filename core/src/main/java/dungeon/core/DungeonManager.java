package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.Gdx;  // Importa a classe Gdx da libGDX para acesso a funções de tempo e de input
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa SpriteBatch para renderizar elementos gráficos
import java.util.HashMap;  // Importa HashMap para armazenar as salas do jogo
import java.util.Map;  // Importa a interface Map usada para declarar a coleção de salas
import java.util.Random;  // Importa a classe Random para gerar números aleatórios


public class DungeonManager {
    // Instância única (padrão Singleton)
    private static DungeonManager instance;  // Variável estática que guarda a única instância da classe
    
    private Map<Integer, Room> rooms;  // Mapa que associa números identificadores (IDs) às salas do jogo
    private int currentRoomId;  // ID da sala atual onde o jogador se encontra
    
        private Player player;  // Personagem principal controlado pelo utilizador
    
   private Random random;  // Gera números aleatórios para criar variação no comportamento dos inimigos e na distribuição de itens
    
    // Largura e altura da janela do jogo utilizadas para calcular as coordenadas do jogador, dos inimigos, das portas e dos itens
    private static final int SCREEN_WIDTH = 800;  // Largura da janela do jogo em pixels
    private static final int SCREEN_HEIGHT = 600;  // Altura da janela do jogo em pixels
    
    
    private DungeonManager() {  // Construtor para implementar o padrão Singleton
        rooms = new HashMap<>();  // Inicializa o mapa de salas vazio
        random = new Random();  // Cria um novo gerador de números aleatórios
        
        player = new Player((SCREEN_WIDTH - 128) / 2, (SCREEN_HEIGHT - 128) / 2);  // Cria o jogador no centro do ecrã. 128 é a largura/altura do jogador.
        
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
    private void generateDungeon() {  // Método que cria e configura todas as salas do jogo
        // Sala inicial (sala 0)
        Room startRoom = new Room(0, "background.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Cria a sala inicial com ID 0
        
        // Adiciona inimigos à sala inicial
        startRoom.addEnemy(new Enemy(500, 300, Enemy.EnemyType.ZOMBIE, Enemy.AIType.BASIC_FOLLOWER));  // Adiciona um zombie que segue o jogador
        
        // Adiciona itens à sala inicial
        startRoom.addItem(new Item(200, 200, Item.ItemType.HEALTH_POTION));  // Adiciona uma poção de vida
        startRoom.addItem(new Item(600, 400, Item.ItemType.KEY));  // Adiciona uma chave para abrir portas trancadas
        
        // Sala norte (sala 1)
        Room northRoom = new Room(1, "background2.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Cria a sala norte com ID 1
        northRoom.addEnemy(new Enemy(300, 300, Enemy.EnemyType.SKELETON, Enemy.AIType.RANDOM_MOVEMENT));  // Adiciona um esqueleto com movimento aleatório
        northRoom.addEnemy(new Enemy(500, 200, Enemy.EnemyType.ZOMBIE, Enemy.AIType.BASIC_FOLLOWER));  // Adiciona um zombie perseguidor
        
        // Sala leste (sala 2)
        Room eastRoom = new Room(2, "background3.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Cria a sala leste com ID 2
        eastRoom.addEnemy(new Enemy(300, 400, Enemy.EnemyType.SKELETON, Enemy.AIType.PATROL_AREA));  // Adiciona um esqueleto que patrulha uma área
        eastRoom.addEnemy(new Enemy(500, 300, Enemy.EnemyType.SKELETON, Enemy.AIType.RANDOM_MOVEMENT));  // Adiciona um esqueleto com movimento aleatório
        
        // Sala secreta (sala 3)
        Room secretRoom = new Room(3, "background4.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Cria a sala secreta com ID 3
        secretRoom.addEnemy(new Enemy(400, 300, Enemy.EnemyType.BOSS, Enemy.AIType.BASIC_FOLLOWER));  // Adiciona um chefe (boss) que persegue o jogador
        secretRoom.addItem(new Item(300, 200, Item.ItemType.DAMAGE_BOOST));  // Adiciona um item que aumenta o dano do jogador
        secretRoom.addItem(new Item(500, 200, Item.ItemType.HEALTH_POTION));  // Adiciona uma poção de vida
        secretRoom.addItem(new Item(400, 300, Item.ItemType.GOLD_COIN));  // Adiciona moedas de ouro
        
        // Configura as ligações entre as salas através das portas
        // Sala 0 -> Sala 1 (Norte)
        Door doorNorth = new Door(SCREEN_WIDTH / 2, SCREEN_HEIGHT, Room.DoorPosition.NORTH, 1, false);  // Cria porta para a sala norte, desbloqueada
        startRoom.setDoor(Room.DoorPosition.NORTH, doorNorth);  // Adiciona a porta à sala inicial
        
        // Sala 1 -> Sala 0 (Sul)
        Door doorSouth = new Door(SCREEN_WIDTH / 2, 0, Room.DoorPosition.SOUTH, 0, false);  // Cria porta de volta para a sala inicial
        northRoom.setDoor(Room.DoorPosition.SOUTH, doorSouth);  // Adiciona a porta à sala norte
        
        // Sala 0 -> Sala 2 (Leste)
        Door doorEast = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 2, false);  // Cria porta para a sala leste
        startRoom.setDoor(Room.DoorPosition.EAST, doorEast);  // Adiciona a porta à sala inicial
        
        // Sala 2 -> Sala 0 (Oeste)
        Door doorWest = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 0, false);  // Cria porta de volta para a sala inicial
        eastRoom.setDoor(Room.DoorPosition.WEST, doorWest);  // Adiciona a porta à sala leste
        
        // Sala 2 -> Sala 3 (Leste, porta trancada que requer chave)
        Door secretDoor = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 3, true);  // Cria porta trancada para a sala secreta
        eastRoom.setDoor(Room.DoorPosition.EAST, secretDoor);  // Adiciona a porta à sala leste
        
        // Sala 3 -> Sala 2 (Oeste, saída da sala secreta)
        Door returnDoor = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 2, false);  // Cria porta de saída da sala secreta
        secretRoom.setDoor(Room.DoorPosition.WEST, returnDoor);  // Adiciona a porta à sala secreta
        
        // Adiciona todas as salas ao mapa do jogo
        rooms.put(0, startRoom);  // Adiciona a sala inicial ao mapa de salas
        rooms.put(1, northRoom);  // Adiciona a sala norte (cima) ao mapa de salas
        rooms.put(2, eastRoom);  // Adiciona a sala leste ao mapa de salas
        rooms.put(3, secretRoom);  // Adiciona a sala secreta ao mapa de salas
        
        // Define a sala inicial para o jogador começar
        currentRoomId = 0;  // O jogador começa na sala com ID 0 (sala inicial)
    }
    
    
    public void update(float deltaTime) {  // Método chamado a cada frame para atualizar o estado do jogo
        // Atualiza a posição e o estado do jogador
        player.update(deltaTime);  // Atualiza a posição e estado do jogador com base nas teclas pressionadas
        
        // Atualiza a sala atual e tudo o que está dentro dela
        Room currentRoom = rooms.get(currentRoomId);  // Obtém a referência para a sala atual
        currentRoom.update(deltaTime, player);  // Atualiza a sala atual, incluindo inimigos e itens
        
        // Verifica se o jogador está a tentar mudar de sala
        if (player.isInRoomTransition()) {  // Verifica se o jogador está a interagir com uma porta para mudar de sala
            handleRoomTransition();  // Processa a mudança de sala
        }
    }
    
    
    public void render(SpriteBatch batch) {  // Método chamado a cada frame para desenhar o jogo
        // Desenha a sala atual
        rooms.get(currentRoomId).render(batch);  // Desenha a sala atual, incluindo fundo, portas, inimigos e itens
        
        // Desenha o jogador
        player.render(batch);  // Desenha o jogador por cima da sala
    }
    
    /*
      Processa a transição do jogador entre salas diferentes.
      Quando o jogador usa uma porta, este método é chamado para mudar para a nova sala e reposicionar o jogador adequadamente.
     */
    private void handleRoomTransition() {  // Método que gerencia a mudança de sala quando o jogador usa uma porta
        Room.DoorPosition exitDirection = player.getExitDirection();  // Obtém a direção da saída (porta usada)
        Room currentRoom = rooms.get(currentRoomId);  // Obtém a sala atual
        Door exitDoor = currentRoom.getDoor(exitDirection);  // Obtém a porta de saída usada pelo jogador
        
        if (exitDoor != null && !exitDoor.isLocked()) {  // Verifica se a porta existe e não está trancada
            int nextRoomId = exitDoor.getTargetRoomId();  // Obtém o ID da sala de destino
            Room nextRoom = rooms.get(nextRoomId);  // Obtém a referência para a próxima sala
            
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
    
    /*
      Gestão dos recursos gráficos usados pelo jogo.
      Importante chamar quando o jogo termina para evitar memory leaks.
     */
    public void dispose() {  // Método para libertar os recursos quando o jogo termina
        for (Room room : rooms.values()) {  // Percorre todas as salas
            room.dispose();  // Liberta os recursos de cada sala
        }
        player.dispose();  // Liberta os recursos do jogador
    }
    
    public Player getPlayer() {  // Método que permite aceder ao personagem do jogador a partir de outras classes
		return player;  // Devolve a referência ao personagem controlado pelo utilizador
	}
    
    public Room getCurrentRoom() {  // Método que fornece acesso à sala atual
        return rooms.get(currentRoomId);  // Devolve a referência à sala onde o jogador está
    }
    
    public int getCurrentRoomId() {  // Método que devolve o ID da sala atual
        return currentRoomId;  // Devolve o ID numérico da sala atual
    }
}
