package dungeon.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Gerencia o dungeon inteiro, incluindo todas as salas e a navegação entre elas.
 * Implementa o padrão Singleton para acesso global.
 */
public class DungeonManager {
    // Singleton
    private static DungeonManager instance;

    // Salas do dungeon
    private Map<Integer, Room> rooms;
    private int currentRoomId;

    // Jogador
    private Player player;

    // Geração aleatória
    private Random random;

    // Constantes
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    // Privado para Singleton
    private DungeonManager() {
        rooms = new HashMap<>();
        random = new Random();

        // Inicializa o jogador
        player = new Player((SCREEN_WIDTH - 128) / 2, (SCREEN_HEIGHT - 128) / 2);

        // Cria o dungeon
        generateDungeon();
    }

    // Obtém a instância Singleton
    public static DungeonManager getInstance() {
        if (instance == null) {
            instance = new DungeonManager();
        }
        return instance;
    }

    /**
     * Gera um dungeon básico com 4 salas conectadas.
     * Pode ser expandido para usar algoritmos mais complexos como BSP.
     */
    private void generateDungeon() {
        // Sala inicial (sala 0)
        Room startRoom = new Room(0, "background.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);

        // Adiciona alguns inimigos à sala inicial
        startRoom.addEnemy(new Enemy(500, 300, Enemy.EnemyType.ZOMBIE, Enemy.AIType.BASIC_FOLLOWER));

        // Adiciona alguns itens à sala inicial
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

        // Configurar portas
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

        // Sala 2 -> Sala 3 (Leste, trancada)
        Door secretDoor = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 3, true); // Porta trancada
        eastRoom.setDoor(Room.DoorPosition.EAST, secretDoor);

        // Sala 3 -> Sala 2 (Oeste)
        Door returnDoor = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 2, false);
        secretRoom.setDoor(Room.DoorPosition.WEST, returnDoor);

        // Adicionar todas as salas ao mapa
        rooms.put(0, startRoom);
        rooms.put(1, northRoom);
        rooms.put(2, eastRoom);
        rooms.put(3, secretRoom);

        // Definir a sala inicial
        currentRoomId = 0;
    }

    // Atualiza o estado do dungeon
    public void update(float deltaTime) {
        // Atualiza o jogador
        player.update(deltaTime);

        // Atualiza a sala atual
        Room currentRoom = rooms.get(currentRoomId);
        currentRoom.update(deltaTime, player);

        // Verifica transição de sala
        if (player.isInRoomTransition()) {
            handleRoomTransition();
        }
    }

    // Renderiza o dungeon
    public void render(SpriteBatch batch) {
        // Renderiza a sala atual
        rooms.get(currentRoomId).render(batch);

        // Renderiza o jogador
        player.render(batch);
    }

    // Lida com a transição entre salas
    private void handleRoomTransition() {
        Room.DoorPosition exitDirection = player.getExitDirection();
        Room currentRoom = rooms.get(currentRoomId);
        Door exitDoor = currentRoom.getDoor(exitDirection);

        if (exitDoor != null && !exitDoor.isLocked()) {
            int nextRoomId = exitDoor.getTargetRoomId();
            Room nextRoom = rooms.get(nextRoomId);

            if (nextRoom != null) {
                // Posição da entrada na nova sala
                Room.DoorPosition entryDirection = exitDirection.getOpposite();
                Door entryDoor = nextRoom.getDoor(entryDirection);

                // Reposicionar o jogador próximo à porta de entrada
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

                // Atualizar a sala atual
                currentRoomId = nextRoomId;
                System.out.println("Mudando para a sala " + currentRoomId);
            }
        }

        // Resetar a flag de transição
        player.resetRoomTransition();
    }

    // Libera recursos
    public void dispose() {
        for (Room room : rooms.values()) {
            room.dispose();
        }
        player.dispose();
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomId);
    }

    public int getCurrentRoomId() {
        return currentRoomId;
    }
}
