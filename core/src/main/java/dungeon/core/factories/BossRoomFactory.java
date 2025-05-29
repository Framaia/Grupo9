package dungeon.core.factories;  // Define o pacote onde as factories estão localizadas

import dungeon.core.*;  // Importa as classes principais do jogo
import dungeon.core.ai.BasicFollowerStrategy;  // Importa a estratégia básica

// Factory para criar a sala do boss final
public class BossRoomFactory extends RoomFactory {

    @Override
    protected Room buildRoom(int id) {  // Cria estrutura da sala do boss
        return new Room(id, "background4.jpg", SCREEN_WIDTH, SCREEN_HEIGHT);  // Sala com fundo especial
    }

    @Override
    protected void populateWithEnemies(Room room) {  // Adiciona boss e guardas
        // Boss principal no centro-direita
        Enemy boss = new Enemy(SCREEN_WIDTH * 0.7f, SCREEN_HEIGHT * 0.5f,
            Enemy.EnemyType.BOSS, new BasicFollowerStrategy());
        room.addEnemy(boss);  // Adiciona boss à sala

        // Guardas do boss em formação
        Enemy guard1 = new Enemy(SCREEN_WIDTH * 0.8f, SCREEN_HEIGHT * 0.3f,
            Enemy.EnemyType.SKELETON, new BasicFollowerStrategy());
        room.addEnemy(guard1);  // Guarda superior

        Enemy guard2 = new Enemy(SCREEN_WIDTH * 0.8f, SCREEN_HEIGHT * 0.7f,
            Enemy.EnemyType.SKELETON, new BasicFollowerStrategy());
        room.addEnemy(guard2);  // Guarda inferior

        Enemy guard3 = new Enemy(SCREEN_WIDTH * 0.6f, SCREEN_HEIGHT * 0.2f,
            Enemy.EnemyType.ZOMBIE, new BasicFollowerStrategy());
        room.addEnemy(guard3);  // Guarda zombie esquerdo superior

        Enemy guard4 = new Enemy(SCREEN_WIDTH * 0.6f, SCREEN_HEIGHT * 0.8f,
            Enemy.EnemyType.ZOMBIE, new BasicFollowerStrategy());
        room.addEnemy(guard4);  // Guarda zombie esquerdo inferior
    }

    @Override
    protected void populateWithItems(Room room) {  // Adiciona tesouros valiosos
        // Poção de vida poderosa
        Item healthPotion = new Item(SCREEN_WIDTH * 0.6f, SCREEN_HEIGHT * 0.3f, Item.ItemType.HEALTH_POTION);
        room.addItem(healthPotion);  // Poção para ajudar contra o boss

        // Grande quantidade de ouro como recompensa
        Item gold = new Item(SCREEN_WIDTH * 0.8f, SCREEN_HEIGHT * 0.3f, Item.ItemType.GOLD_COIN);
        room.addItem(gold);  // Tesouro do boss

        // Item de aumento de dano
        Item damageBoost = new Item(SCREEN_WIDTH * 0.7f, SCREEN_HEIGHT * 0.2f, Item.ItemType.DAMAGE_BOOST);
        room.addItem(damageBoost);  // Melhoria permanente
    }

    @Override
    protected void addDoors(Room room) {  // Adiciona apenas porta de saída
        // Porta de retorno para sala anterior
        Door westDoor = new Door(0, SCREEN_HEIGHT / 2, Room.DoorPosition.WEST, 2, false);
        room.setDoor(Room.DoorPosition.WEST, westDoor);  // Saída desbloqueada
    }
}
