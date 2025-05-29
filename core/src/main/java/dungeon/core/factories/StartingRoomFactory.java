package dungeon.core.factories;  // Define o pacote onde as factories estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo
import dungeon.core.ai.BasicFollowerStrategy;  // Importa a estratégia básica

// Factory específica para criar a sala inicial do jogo
public class StartingRoomFactory extends RoomFactory {

    @Override
    protected Room buildRoom(int id) {  // Cria a estrutura da sala inicial
        return new Room(id, "background.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Sala com fundo básico
    }

    @Override
    protected void populateWithEnemies(Room room) {  // Adiciona inimigos apropriados para iniciantes
        // Adiciona um zombie básico que segue o jogador - inimigo fácil para começar
        Enemy zombie = new Enemy(500, 300, Enemy.EnemyType.ZOMBIE, new BasicFollowerStrategy());
        room.addEnemy(zombie);  // Insere o inimigo na sala
    }

    @Override
    protected void populateWithItems(Room room) {  // Adiciona itens úteis para o início
        // Poção de vida para ajudar o jogador a aprender
        Item healthPotion = new Item(200, 200, Item.ItemType.HEALTH_POTION);
        room.addItem(healthPotion);  // Adiciona poção à sala

        // Chave para abrir portas trancadas
        Item key = new Item(600, 400, Item.ItemType.KEY);
        room.addItem(key);  // Adiciona chave à sala
    }

    @Override
    protected void addDoors(Room room) {  // Adiciona portas de saída da sala inicial
        // Porta para norte (sala 1) - desbloqueada
        Door northDoor = new Door(SCREEN_WIDTH / 2, SCREEN_HEIGHT, Room.DoorPosition.NORTH, 1, false);
        room.setDoor(Room.DoorPosition.NORTH, northDoor);  // Define porta norte

        // Porta para leste (sala 2) - desbloqueada
        Door eastDoor = new Door(SCREEN_WIDTH, SCREEN_HEIGHT / 2, Room.DoorPosition.EAST, 2, false);
        room.setDoor(Room.DoorPosition.EAST, eastDoor);  // Define porta leste
    }
}
