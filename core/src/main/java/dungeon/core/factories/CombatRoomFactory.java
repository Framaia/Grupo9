package dungeon.core.factories;  // Define o pacote onde as factories estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo
import dungeon.core.ai.BasicFollowerStrategy;  // Importa a estratégia básica

// Factory para criar salas com dificuldade média
public class CombatRoomFactory extends RoomFactory {
    private int targetRoomId;  // ID da sala que esta factory vai criar

    public CombatRoomFactory(int targetRoomId) {  // Construtor que define qual sala criar
        super();  // Chama construtor da classe pai
        this.targetRoomId = targetRoomId;  // Armazena ID da sala alvo
    }

    @Override
    protected Room buildRoom(int id) {  // Cria estrutura de sala de combate
        String background = "background" + (id + 1) + ".jpg";  // Nome do ficheiro baseado no ID
        return new Room(id, background, SCREEN_WIDTH, SCREEN_HEIGHT);  // Sala com fundo específico
    }

    @Override
    protected void populateWithEnemies(Room room) {  // Adiciona inimigos de dificuldade média
        if (targetRoomId == 1) {  // Sala norte
            // Esqueleto e zombie
            room.addEnemy(new Enemy(300, 300, Enemy.EnemyType.SKELETON, new BasicFollowerStrategy()));
            room.addEnemy(new Enemy(500, 200, Enemy.EnemyType.ZOMBIE, new BasicFollowerStrategy()));
        } else if (targetRoomId == 2) {  // Sala leste
            // Dois esqueletos
            room.addEnemy(new Enemy(300, 400, Enemy.EnemyType.SKELETON, new BasicFollowerStrategy()));
            room.addEnemy(new Enemy(500, 300, Enemy.EnemyType.SKELETON, new BasicFollowerStrategy()));
        }
    }

    @Override
    protected void populateWithItems(Room room) {  // Adiciona itens de recompensa média
        // Adiciona chave se for sala 1
        if (targetRoomId == 1) {
            Item key = new Item(300, 400, Item.ItemType.KEY);
            room.addItem(key);
        }

        // Sempre adiciona moedas de ouro
        Item gold = new Item(400, 350, Item.ItemType.GOLD_COIN);
        room.addItem(gold);
    }

    @Override
    protected void addDoors(Room room) {  // Adiciona portas baseadas no ID da sala
        if (targetRoomId == 1) {  // Sala norte
            // Porta de retorno para sala inicial
            Door southDoor = new Door(SCREEN_WIDTH / 2, 0, Room.DoorPosition.SOUTH, 0, false);
            room.setDoor(Room.DoorPosition.SOUTH, southDoor);
        } else if (targetRoomId == 2) {  // Sala leste
            // Porta de retorno para sala inicial
            Door westDoor = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 0, false);
            room.setDoor(Room.DoorPosition.WEST, westDoor);

            // Porta para sala secreta - trancada
            Door eastDoor = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 3, true);
            room.setDoor(Room.DoorPosition.EAST, eastDoor);
        }
    }
}
