package dungeon.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    // Enumeração para a posição das portas
    public enum DoorPosition {
        NORTH, SOUTH, EAST, WEST;

        public DoorPosition getOpposite() {
            switch (this) {
                case NORTH: return SOUTH;
                case SOUTH: return NORTH;
                case EAST: return WEST;
                case WEST: return EAST;
                default: return null;
            }
        }
    }

    // ID da sala
    private int id;

    // Dimensões da sala
    private int width, height;

    // Componente visual
    private Texture backgroundTexture;

    // Conteúdo da sala
    private List<Enemy> enemies;
    private List<Item> items;
    private Door[] doors; // Norte, Sul, Leste, Oeste

    // Estado da sala
    private boolean visited;
    private boolean cleared;

    // Gerador de números aleatórios
    private Random random;

    // Construtor
    public Room(int id, String backgroundPath, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.backgroundTexture = new Texture(backgroundPath);
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.doors = new Door[4]; // Norte, Sul, Leste, Oeste
        this.visited = false;
        this.cleared = false;
        this.random = new Random();
    }

    // Atualiza o estado da sala
    public void update(float deltaTime, Player player) {
        // Marcar como visitada
        visited = true;

        // Atualizar todos os inimigos
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                enemy.update(deltaTime, player);
            }
        }

        // Atualizar todos os itens
        for (Item item : items) {
            item.update(deltaTime);

            // Verificar colisão com o jogador e coletar item
            if (!item.isCollected() && item.checkCollision(player)) {
                collectItem(item, player);
            }
        }

        // Verificar se a sala foi limpa (todos os inimigos derrotados)
        if (!cleared && areAllEnemiesDead()) {
            cleared = true;
            generateRewards(); // Gera recompensas quando todos os inimigos são derrotados
        }

        // Verificar colisão com portas
        for (int i = 0; i < doors.length; i++) {
            if (doors[i] != null) {
                if (doors[i].checkCollision(player)) {
                    // Porta trancada requer chave
                    if (doors[i].isLocked()) {
                        if (player.hasKey()) {
                            doors[i].unlock();
                            player.useKey();
                            System.out.println("Porta desbloqueada com uma chave!");
                        } else {
                            System.out.println("Esta porta está trancada. Você precisa de uma chave!");
                        }
                    }
                    // Porta destrancada permite transição
                    else if (!doors[i].isLocked()) {
                        // Notifica o sistema que o jogador quer mudar de sala
                        player.setRoomTransition(true, DoorPosition.values()[i]);
                    }
                }
            }
        }
    }

    // Renderiza a sala e seu conteúdo
    public void render(SpriteBatch batch) {
        // Desenhar o fundo
        batch.draw(backgroundTexture, 0, 0, width, height);

        // Desenhar as portas
        for (Door door : doors) {
            if (door != null) {
                door.render(batch);
            }
        }

        // Desenhar os itens
        for (Item item : items) {
            item.render(batch);
        }

        // Desenhar os inimigos
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                enemy.render(batch);
                enemy.drawHealthBar(batch, backgroundTexture);
            }
        }
    }

    // Verifica se todos os inimigos foram derrotados
    private boolean areAllEnemiesDead() {
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                return false;
            }
        }
        return true;
    }

    // Gera recompensas quando a sala é limpa
    private void generateRewards() {
        // Chance de gerar moedas e outros itens
        int rewardCount = random.nextInt(3) + 1; // 1-3 recompensas

        for (int i = 0; i < rewardCount; i++) {
            // Posição aleatória na sala
            float x = random.nextFloat() * (width - 32);
            float y = random.nextFloat() * (height - 32);

            // Tipo de item aleatório
            int itemType = random.nextInt(10);
            if (itemType < 5) {
                // 50% chance de moedas
                items.add(new Item(x, y, Item.ItemType.GOLD_COIN));
            } else if (itemType < 8) {
                // 30% chance de poção de vida
                items.add(new Item(x, y, Item.ItemType.HEALTH_POTION));
            } else if (itemType < 9) {
                // 10% chance de buff de dano
                items.add(new Item(x, y, Item.ItemType.DAMAGE_BOOST));
            } else {
                // 10% chance de chave
                items.add(new Item(x, y, Item.ItemType.KEY));
            }
        }

        System.out.println("Sala limpa! " + rewardCount + " recompensas geradas!");
    }

    // Processa a coleta de um item pelo jogador
    private void collectItem(Item item, Player player) {
        item.collect();

        // Aplicar efeito do item
        switch (item.getType()) {
            case HEALTH_POTION:
                player.heal(item.getType().getValue());
                System.out.println("Você coletou uma " + item.getType().getName() + "! +"
                    + item.getType().getValue() + " de vida.");
                break;

            case DAMAGE_BOOST:
                player.increaseDamage(item.getType().getValue());
                System.out.println("Você coletou um " + item.getType().getName() + "! +"
                    + item.getType().getValue() + " de dano.");
                break;

            case KEY:
                player.addKey();
                System.out.println("Você coletou uma " + item.getType().getName() + "!");
                break;

            case GOLD_COIN:
                player.addGold(item.getType().getValue());
                System.out.println("Você coletou " + item.getType().getValue() + " moedas de ouro!");
                break;
        }
    }

    // Adiciona um inimigo à sala
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    // Adiciona um item à sala
    public void addItem(Item item) {
        items.add(item);
    }

    // Define uma porta em uma posição específica
    public void setDoor(DoorPosition position, Door door) {
        doors[position.ordinal()] = door;
    }

    // Retorna a porta em uma posição específica
    public Door getDoor(DoorPosition position) {
        return doors[position.ordinal()];
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isCleared() {
        return cleared;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Libera recursos
    public void dispose() {
        backgroundTexture.dispose();

        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        for (Item item : items) {
            item.dispose();
        }

        for (Door door : doors) {
            if (door != null) {
                door.dispose();
            }
        }
    }
}
