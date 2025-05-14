package dungeon.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
  Classe DungeonManager - Gere todo o jogo e coordena a navegação entre salas.
  Esta classe usa o padrão Singleton para garantir que só existe uma instância em todo o jogo,
  permitindo acesso global quando necessário.
*/
public class DungeonManager {
    // Instância única (padrão Singleton)
    private static DungeonManager instance;

    // Coleção de salas que compõem o jogo
    private Map<Integer, Room> rooms;
    private int currentRoomId;  // ID da sala atual onde o jogador se encontra

    // Personagem principal controlado pelo jogador
    private Player player;

    // Gerador de números aleatórios para eventos baseados em probabilidade
    private Random random;

    // Tamanho do ecrã para posicionamento dos elementos
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    /*
      Construtor privado (parte do padrão Singleton).
      Inicializa o jogo e o jogador e gera o labirinto de salas.
      É privado para que não se possam criar múltiplas instâncias.
     */
    private DungeonManager() {
        rooms = new HashMap<>();
        random = new Random();

        // Cria o jogador no centro do ecrã
        player = new Player((SCREEN_WIDTH - 128) / 2, (SCREEN_HEIGHT - 128) / 2);

        // Constrói a estrutura do jogo
        generateDungeon();
    }

    /*
      Obtém a instância única do DungeonManager.
      Se ainda não existir, cria uma nova instância.
      É parte essencial do padrão Singleton.
     */
    public static DungeonManager getInstance() {
        if (instance == null) {
            instance = new DungeonManager();
        }
        return instance;
    }

    /*
      Gera o jogo completo com várias salas interligadas.
      Configura os inimigos, os itens e as portas em cada sala.
      Atualmente cria um layout básico com 4 salas, mas pode ser expandido para usar algoritmos mais complexos.
     */
    private void generateDungeon() {
        // Sala inicial (sala 0)
        Room startRoom = new Room(0, "background.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);

        // Adiciona inimigos à sala inicial
        startRoom.addEnemy(new Enemy(500, 300, Enemy.EnemyType.ZOMBIE, Enemy.AIType.BASIC_FOLLOWER));

        // Adiciona itens à sala inicial
        startRoom.addItem(new Item(200, 200, Item.ItemType.HEALTH_POTION));
        startRoom.addItem(new Item(600, 400, Item.ItemType.KEY));

        // Sala norte (sala 1)
        Room northRoom = new Room(1, "background2.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);
        northRoom.addEnemy(new Enemy(300, 300, Enemy.EnemyType.SKELETON, Enemy.AIType.RANDOM_MOVEMENT));
        northRoom.addEnemy(new Enemy(500, 200, Enemy.EnemyType.ZOMBIE, Enemy.AIType.BASIC_FOLLOWER));

        // Sala leste (sala 2)
        Room eastRoom = new Room(2, "background3.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);
        eastRoom.addEnemy(new Enemy(300, 400, Enemy.EnemyType.SKELETON, Enemy.AIType.PATROL_AREA));
        eastRoom.addEnemy(new Enemy(500, 300, Enemy.EnemyType.SKELETON, Enemy.AIType.RANDOM_MOVEMENT));

        // Sala secreta (sala 3)
        Room secretRoom = new Room(3, "background4.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);
        secretRoom.addEnemy(new Enemy(400, 300, Enemy.EnemyType.BOSS, Enemy.AIType.BASIC_FOLLOWER));
        secretRoom.addItem(new Item(300, 200, Item.ItemType.DAMAGE_BOOST));
        secretRoom.addItem(new Item(500, 200, Item.ItemType.HEALTH_POTION));
        secretRoom.addItem(new Item(400, 300, Item.ItemType.GOLD_COIN));

        // Configura as ligações entre as salas através das portas
        // Sala 0 -> Sala 1 (Norte)
        Door doorNorth = new Door(SCREEN_WIDTH / 2, SCREEN_HEIGHT, Room.DoorPosition.NORTH, 1, false);
        startRoom.setDoor(Room.DoorPosition.NORTH, doorNorth);

        // Sala 1 -> Sala 0 (Sul)
        Door doorSouth = new Door(SCREEN_WIDTH / 2, 0, Room.DoorPosition.SOUTH, 0, false);
        northRoom.setDoor(Room.DoorPosition.SOUTH, doorSouth);

        // Sala 0 -> Sala 2 (Leste)
        Door doorEast = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 2, false);
        startRoom.setDoor(Room.DoorPosition.EAST, doorEast);

        // Sala 2 -> Sala 0 (Oeste)
        Door doorWest = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 0, false);
        eastRoom.setDoor(Room.DoorPosition.WEST, doorWest);

        // Sala 2 -> Sala 3 (Leste, porta trancada que requer chave)
        Door secretDoor = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 3, true);
        eastRoom.setDoor(Room.DoorPosition.EAST, secretDoor);

        // Sala 3 -> Sala 2 (Oeste, retorno da sala secreta)
        Door returnDoor = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 2, false);
        secretRoom.setDoor(Room.DoorPosition.WEST, returnDoor);

        // Adiciona todas as salas ao mapa do jogo
        rooms.put(0, startRoom);
        rooms.put(1, northRoom);
        rooms.put(2, eastRoom);
        rooms.put(3, secretRoom);

        // Define a sala inicial para o jogador começar
        currentRoomId = 0;
    }

    /*
      Atualiza o estado do jogo a cada frame.
	  Processa as ações do jogador e dos inimigos e também verifica as transições entre as salas.
	  O parâmetro "deltaTime" representa o tempo decorrido desde o último frame, permitindo movimentos consistentes 
      independentemente da velocidade do computador.
      */
    public void update(float deltaTime) {
        // Atualiza a posição e o estado do jogador
        player.update(deltaTime);

        // Atualiza a sala atual e tudo o que está dentro dela
        Room currentRoom = rooms.get(currentRoomId);
        currentRoom.update(deltaTime, player);

        // Verifica se o jogador está a tentar mudar de sala
        if (player.isInRoomTransition()) {
            handleRoomTransition();
        }
    }

    /*
      Desenha todos os elementos do jogo no ecrã.
      Exibe a sala atual e o jogador usando o SpriteBatch fornecido.
	  */
    public void render(SpriteBatch batch) {
        // Desenha a sala atual
        rooms.get(currentRoomId).render(batch);

        // Desenha o jogador
        player.render(batch);
    }

    /*
      Processa a transição do jogador entre salas diferentes.
      Quando o jogador usa uma porta, este método é chamado para mudar para a nova sala e reposicionar o jogador adequadamente.
     */
    private void handleRoomTransition() {
        Room.DoorPosition exitDirection = player.getExitDirection();
        Room currentRoom = rooms.get(currentRoomId);
        Door exitDoor = currentRoom.getDoor(exitDirection);

        if (exitDoor != null && !exitDoor.isLocked()) {
            int nextRoomId = exitDoor.getTargetRoomId();
            Room nextRoom = rooms.get(nextRoomId);

            if (nextRoom != null) {
                // Determina a posição de entrada na nova sala
                Room.DoorPosition entryDirection = exitDirection.getOpposite();
                Door entryDoor = nextRoom.getDoor(entryDirection);

                // Reposiciona o jogador junto à porta de entrada
                float newX = 0;
                float newY = 0;

                switch (entryDirection) {
                    case NORTH:
                        newX = SCREEN_WIDTH / 2 - player.getWidth() / 2;
                        newY = SCREEN_HEIGHT - player.getHeight() - 20;
                        break;
                    case SOUTH:
                        newX = SCREEN_WIDTH / 2 - player.getWidth() / 2;
                        newY = 20;
                        break;
                    case EAST:
                        newX = SCREEN_WIDTH - player.getWidth() - 20;
                        newY = SCREEN_HEIGHT / 2 - player.getHeight() / 2;
                        break;
                    case WEST:
                        newX = 20;
                        newY = SCREEN_HEIGHT / 2 - player.getHeight() / 2;
                        break;
                }

                player.setPosition(newX, newY);

                // Atualiza o ID da sala atual
                currentRoomId = nextRoomId;
                System.out.println("Mudando para a sala " + currentRoomId);
            }
        }

        // Limpa a flag de transição do jogador
        player.resetRoomTransition();
    }

    /*
      Gestão dos recursos gráficos usados pelo jogo.
      Importante chamar quando o jogo termina para evitar memory leaks.
     */
    public void dispose() {
        for (Room room : rooms.values()) {
            room.dispose();
        }
        player.dispose();
    }

    /*
      Devolve a instância do personagem controlado pelo jogador.
	  Útil para outras classes que precisem de interagir com o jogador.
     */
    public Player getPlayer() {
        return player;
    }

    /*
      Devolve a sala atual que o jogador está a explorar.
	  Permite acesso aos conteúdos e características da sala.
     */
    public Room getCurrentRoom() {
        return rooms.get(currentRoomId);
    }

    /*
     * Devolve o número identificador (ID) da sala atual.
	   Serve para identificar qual das salas  está ativa neste momento.
     */
    public int getCurrentRoomId() {
        return currentRoomId;
    }
}
