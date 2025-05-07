package dungeon.core;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameplayScreen extends ScreenAdapter {
    // Referência ao jogo principal
    private MainGame game;

    // Elementos de renderização
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    // Estado do jogo
    private boolean paused = false;
    private boolean gameOver = false;
    private String statusMessage = "";
    private float messageTimer = 0;

    // Sala atual
    private int currentRoom = 0;
    private final int TOTAL_ROOMS = 4;

    // Texturas
    private Texture[] backgroundTextures;
    private Texture playerTexture;
    private Texture[] enemyTextures; // diferentes tipos de inimigos
    private Texture gameOverTexture;
    private Texture pauseOverlayTexture;
    private Texture[] itemTextures; // diferentes tipos de itens
    private Texture attackEffectTexture;
    private Texture[] doorTextures; // diferentes direções de portas

    // Jogador
    private float playerX, playerY;
    private float playerWidth = 128f;
    private float playerHeight = 128f;
    private float moveSpeed = 300f;
    private int playerHealth = 100;
    private final int MAX_HEALTH = 100;
    private int keys = 0;
    private int gold = 0;

    // Inimigos
    private List<Enemy> enemies = new ArrayList<>();
    private int enemiesKilled = 0;

    // Itens
    private List<Item> items = new ArrayList<>();

    // Portas
    private List<Door> doors = new ArrayList<>();

    // Efeitos visuais
    private boolean showAttackEffect = false;
    private float attackEffectX, attackEffectY;
    private float attackEffectTimer = 0;

    // Utilitários
    private Random random = new Random();

    // Classe para representar inimigos
    private class Enemy {
        float x, y;
        float width, height;
        float speed;
        int health;
        int maxHealth;
        int damage;
        String type;
        Texture texture;
        boolean isDead = false;

        public Enemy(float x, float y, String type, Texture texture, int health, float speed, int damage) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.texture = texture;
            this.health = health;
            this.maxHealth = health;
            this.speed = speed;
            this.damage = damage;
            this.width = 128;
            this.height = 128;
        }

        public void update(float delta) {
            if (isDead) return;

            // Movimento em direção ao jogador
            float dirX = playerX - x;
            float dirY = playerY - y;

            // Normalizar direção
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                dirX /= length;
                dirY /= length;
            }

            // Mover inimigo
            x += dirX * speed * delta;
            y += dirY * speed * delta;
        }

        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
            return !isDead &&
                px < x + width * 0.7f && px + pWidth > x + width * 0.3f &&
                py < y + height * 0.7f && py + pHeight > y + height * 0.3f;
        }

        public void takeDamage(int amount) {
            if (isDead) return;

            health -= amount;
            if (health <= 0) {
                health = 0;
                isDead = true;
                showMessage("Inimigo " + type + " derrotado!");

                // Chance de deixar cair item
                dropItem();
            }
        }

        private void dropItem() {
            float chance = random.nextFloat();

            if (chance < 0.4f) { // 40% poção
                items.add(new Item(x + width/2, y + height/2, "health_potion", itemTextures[0], 20));
                showMessage("O inimigo deixou cair uma poção!");
            } else if (chance < 0.7f) { // 30% moeda
                items.add(new Item(x + width/2, y + height/2, "gold_coin", itemTextures[2], 10));
                showMessage("O inimigo deixou cair moedas de ouro!");
            } else if (chance < 0.8f) { // 10% chave
                items.add(new Item(x + width/2, y + height/2, "key", itemTextures[1], 1));
                showMessage("O inimigo deixou cair uma chave!");
            }
        }
    }

    // Classe para representar itens
    private class Item {
        float x, y;
        float width, height;
        Texture texture;
        String type;
        int value;
        boolean collected = false;
        float bobTimer = 0;

        public Item(float x, float y, String type, Texture texture, int value) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.texture = texture;
            this.value = value;
            this.width = 32;
            this.height = 32;
        }

        public void update(float delta) {
            if (collected) return;

            // Animação de flutuação
            bobTimer += delta;
            y += Math.sin(bobTimer * 5) * 0.5f;
        }

        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
            return !collected &&
                px < x + width && px + pWidth > x &&
                py < y + height && py + pHeight > y;
        }
    }

    // Classe para representar portas
    private class Door {
        float x, y;
        float width, height;
        Texture texture;
        String direction;
        int targetRoom;
        boolean isLocked;

        public Door(float x, float y, String direction, Texture texture, int targetRoom, boolean isLocked) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.texture = texture;
            this.targetRoom = targetRoom;
            this.isLocked = isLocked;

            // Ajustar dimensões baseado na direção
            if (direction.equals("north") || direction.equals("south")) {
                this.width = 128;
                this.height = 64;
            } else {
                this.width = 64;
                this.height = 128;
            }
        }

        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
            return px < x + width && px + pWidth > x &&
                py < y + height && py + pHeight > y;
        }
    }

    public GameplayScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        glyphLayout = new GlyphLayout();

        // Carregar ou criar texturas
        loadTextures();

        // Configuração inicial do jogador
        playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
        playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;

        // Configurar a sala inicial
        setupRoom(currentRoom);

        // Configuração inicial do jogador
        playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
        playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;

        // Configurar a sala inicial
        setupRoom(currentRoom);

    }

    private void loadTextures() {
        // Texturas de fundo para cada sala
        backgroundTextures = new Texture[TOTAL_ROOMS];
        for (int i = 0; i < TOTAL_ROOMS; i++) {
            String fileName = (i == 0) ? "background.jpg" : "background" + (i+1) + ".jpg";
            backgroundTextures[i] = loadTextureOrCreate(fileName, 800, 600, createColor(0.2f + i*0.1f, 0.2f, 0.3f - i*0.05f, 1));
        }

        // Texturas de inimigos
        enemyTextures = new Texture[3]; // zumbi, esqueleto, boss
        enemyTextures[0] = loadTextureOrCreate("enemy.png", 128, 128, Color.RED);
        enemyTextures[1] = loadTextureOrCreate("skeleton.png", 128, 192, Color.LIGHT_GRAY);
        enemyTextures[2] = loadTextureOrCreate("boss.png", 128, 128, Color.PURPLE);

        // Texturas de itens
        itemTextures = new Texture[3]; // poção, chave, moeda
        itemTextures[0] = loadTextureOrCreate("health_potion.png", 32, 32, Color.GREEN);
        itemTextures[1] = loadTextureOrCreate("key.png", 32, 32, Color.YELLOW);
        itemTextures[2] = loadTextureOrCreate("gold_coin.png", 32, 32, Color.GOLD);

        // Texturas de portas
        doorTextures = new Texture[5]; // norte, sul, leste, oeste, trancada
        doorTextures[0] = loadTextureOrCreate("door_north.png", 128, 64, Color.BROWN);
        doorTextures[1] = loadTextureOrCreate("door_south.png", 128, 64, Color.BROWN);
        doorTextures[2] = loadTextureOrCreate("door_east.png", 64, 128, Color.BROWN);
        doorTextures[3] = loadTextureOrCreate("door_west.png", 64, 128, Color.BROWN);
        doorTextures[4] = loadTextureOrCreate("door_locked.png", 128, 64, Color.GRAY);

        // Outras texturas
        playerTexture = loadTextureOrCreate("player.png", 128, 128, Color.BLUE);
        gameOverTexture = loadTextureOrCreate("game_over.jpg", 400, 300, Color.BLACK);
        pauseOverlayTexture = createColorTexture(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, 0, 0, 0.7f));
        attackEffectTexture = loadTextureOrCreate("attack_effect.png", 64, 64, Color.YELLOW);
    }

    private Texture loadTextureOrCreate(String path, int width, int height, Color color) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new Texture(Gdx.files.internal(path));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar textura: " + path + " - " + e.getMessage());
        }

        // Criar textura de fallback
        return createColorTexture(width, height, color);
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Color createColor(float r, float g, float b, float a) {
        return new Color(r, g, b, a);
    }

    private void setupRoom(int roomId) {
        // Limpar listas
        enemies.clear();
        items.clear();
        doors.clear();

        // Configuração específica para cada sala
        switch (roomId) {
            case 0: // Sala inicial
                // Adicionar inimigos
                enemies.add(new Enemy(600, 300, "zombie", enemyTextures[0], 50, 100, 5));

                // Adicionar itens
                items.add(new Item(200, 200, "health_potion", itemTextures[0], 20));
                items.add(new Item(400, 400, "gold_coin", itemTextures[2], 10));

                // Adicionar portas
                doors.add(new Door(Gdx.graphics.getWidth()/2 - 64, Gdx.graphics.getHeight() - 32, "north", doorTextures[0], 1, false));
                doors.add(new Door(Gdx.graphics.getWidth() - 32, Gdx.graphics.getHeight()/2 - 64, "east", doorTextures[2], 2, false));
                break;

            case 1: // Sala norte
                // Adicionar inimigos (mais difíceis)
                enemies.add(new Enemy(300, 300, "skeleton", enemyTextures[1], 70, 120, 8));
                enemies.add(new Enemy(500, 200, "zombie", enemyTextures[0], 50, 100, 5));

                // Adicionar itens
                items.add(new Item(300, 400, "key", itemTextures[1], 1));

                // Adicionar portas
                doors.add(new Door(Gdx.graphics.getWidth()/2 - 64, 32, "south", doorTextures[1], 0, false));
                break;

            case 2: // Sala leste
                // Adicionar inimigos
                enemies.add(new Enemy(300, 400, "skeleton", enemyTextures[1], 70, 120, 8));
                enemies.add(new Enemy(500, 300, "skeleton", enemyTextures[1], 70, 120, 8));

                // Adicionar portas
                doors.add(new Door(32, Gdx.graphics.getHeight()/2 - 64, "west", doorTextures[3], 0, false));
                doors.add(new Door(Gdx.graphics.getWidth() - 32, Gdx.graphics.getHeight()/2 - 64, "east", doorTextures[2], 3, true));
                break;

            case 3: // Sala secreta (sala do boss)
                // Adicionar boss
                enemies.add(new Enemy(400, 300, "boss", enemyTextures[2], 200, 80, 15));

                // Adicionar itens (tesouro)
                items.add(new Item(300, 200, "health_potion", itemTextures[0], 50));
                items.add(new Item(500, 200, "gold_coin", itemTextures[2], 100));

                // Adicionar porta de saída
                doors.add(new Door(32, Gdx.graphics.getHeight()/2 - 64, "west", doorTextures[3], 2, false));
                break;
        }

        // Mensagem de nova sala
        showMessage("Sala " + (roomId + 1));
    }

    @Override
    public void render(float delta) {
        // Limpar a tela
        ScreenUtils.clear(0, 0, 0, 1);

        // Lidar com entrada de pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                showMessage("Jogo Pausado - Pressione ESC para continuar");
            } else {
                showMessage("Jogo Continuado");
            }
        }

        // Se estiver em Game Over, mostrar tela de fim de jogo
        if (gameOver) {
            renderGameOver();
            return;
        }

        // Se não estiver pausado, atualizar a lógica do jogo
        if (!paused) {
            update(delta);
        }

        // Renderizar elementos do jogo
        renderGame();

        // Se estiver pausado, renderizar overlay de pausa
        if (paused) {
            renderPauseOverlay();
        }
    }

    private void update(float delta) {
        // Atualizar mensagem de status
        updateMessage(delta);

        // Atualizar efeito de ataque
        if (showAttackEffect) {
            attackEffectTimer -= delta;
            if (attackEffectTimer <= 0) {
                showAttackEffect = false;
            }
        }

        // Verificar se o jogador está morto
        if (playerHealth <= 0) {
            gameOver = true;
            return;
        }

        // Processar entrada do jogador
        handleInput(delta);

        // Atualizar inimigos
        for (Enemy enemy : enemies) {
            enemy.update(delta);

            // Verificar colisão com o jogador
            if (enemy.isColliding(playerX, playerY, playerWidth, playerHeight)) {
                playerHealth -= enemy.damage * delta;
                if (playerHealth < 0) playerHealth = 0;
            }
        }

        // Atualizar itens
        for (Item item : items) {
            item.update(delta);

            // Verificar colisão com o jogador
            if (item.isColliding(playerX, playerY, playerWidth, playerHeight)) {
                collectItem(item);
            }
        }

        // Verificar colisão com portas
        for (Door door : doors) {
            if (door.isColliding(playerX, playerY, playerWidth, playerHeight)) {
                if (door.isLocked && keys > 0) {
                    door.isLocked = false;
                    keys--;
                    showMessage("Porta desbloqueada! Chaves restantes: " + keys);
                } else if (!door.isLocked) {
                    changeRoom(door.targetRoom);
                    return; // Sai da função após mudar de sala
                } else {
                    showMessage("Esta porta está trancada. Você precisa de uma chave!");
                }
            }
        }


    }

    private void handleInput(float delta) {
        // Movimento
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= moveSpeed * delta;
        }

        // Limitar movimento à tela
        playerX = Math.max(0, Math.min(playerX, Gdx.graphics.getWidth() - playerWidth));
        playerY = Math.max(0, Math.min(playerY, Gdx.graphics.getHeight() - playerHeight));

        // Ataque
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }

    }

    private void attack() {
        boolean hitEnemy = false;

        // Verificar colisão com inimigos
        for (Enemy enemy : enemies) {
            if (!enemy.isDead && isNearby(playerX, playerY, enemy.x, enemy.y, 200)) {
                // Mostrar efeito visual
                showAttackEffect = true;
                attackEffectTimer = 0.3f;
                attackEffectX = enemy.x;
                attackEffectY = enemy.y;

                // Causar dano
                enemy.takeDamage(20);
                hitEnemy = true;

                // Contabilizar inimigo morto
                if (enemy.isDead) {
                    enemiesKilled++;
                }
            }
        }

        if (!hitEnemy) {
            showMessage("Você atacou, mas não acertou nenhum inimigo!");
        }
    }

    private boolean isNearby(float x1, float y1, float x2, float y2, float distance) {
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) <= distance;
    }


private void collectItem(Item item) {
    if (item.collected) return;

    item.collected = true;

    switch (item.type) {
        case "health_potion":
            playerHealth = Math.min(playerHealth + item.value, MAX_HEALTH);
            showMessage("Poção coletada! +" + item.value + " de vida");
            break;

        case "key":
            keys += item.value;
            showMessage("Chave coletada! Total: " + keys);
            break;

        case "gold_coin":
            gold += item.value;
            showMessage("Moedas coletadas! +" + item.value + " ouro");
            break;
    }
}

private void changeRoom(int newRoom) {
    if (newRoom < 0 || newRoom >= TOTAL_ROOMS) return;

    currentRoom = newRoom;
    setupRoom(currentRoom);

    // Ajustar posição do jogador com base na sala anterior
    playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
    playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;
}

private void renderGame() {
    batch.begin();

    // Desenhar o fundo da sala atual
    batch.draw(backgroundTextures[currentRoom], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Desenhar portas
    for (Door door : doors) {
        Texture doorTex = door.isLocked ? doorTextures[4] : door.texture;
        batch.draw(doorTex, door.x, door.y, door.width, door.height);
    }

    // Desenhar itens
    for (Item item : items) {
        if (!item.collected) {
            batch.draw(item.texture, item.x, item.y, item.width, item.height);
        }
    }

    // Desenhar inimigos
    for (Enemy enemy : enemies) {
        if (!enemy.isDead) {
            batch.draw(enemy.texture, enemy.x, enemy.y, enemy.width, enemy.height);
            drawHealthBar(batch, enemy.x, enemy.y + enemy.height + 10, enemy.width, 10, (float)enemy.health / enemy.maxHealth, Color.RED);
        }
    }

    // Desenhar o jogador
    batch.draw(playerTexture, playerX, playerY, playerWidth, playerHeight);

    // Desenhar efeito de ataque
    if (showAttackEffect) {
        batch.draw(attackEffectTexture, attackEffectX, attackEffectY, 64, 64);
    }

    // Desenhar interface
    drawUI();



batch.end();

    }

private void drawUI() {
    // Barra de vida
    drawHealthBar(batch, 10, Gdx.graphics.getHeight() - 30, 200, 20, (float)playerHealth / MAX_HEALTH, Color.GREEN);

    // Informações do jogador
    font.setColor(Color.WHITE);
    font.draw(batch, "Vida: " + playerHealth + "/" + MAX_HEALTH, 10, Gdx.graphics.getHeight() - 40);
    font.draw(batch, "Sala: " + (currentRoom + 1) + "/" + TOTAL_ROOMS, 10, Gdx.graphics.getHeight() - 70);
    font.draw(batch, "Chaves: " + keys, 10, Gdx.graphics.getHeight() - 100);
    font.draw(batch, "Ouro: " + gold, 10, Gdx.graphics.getHeight() - 130);
    font.draw(batch, "Inimigos derrotados: " + enemiesKilled, 10, Gdx.graphics.getHeight() - 160);

    // Mensagem de status
    if (!statusMessage.isEmpty()) {
        font.setColor(Color.YELLOW);
        glyphLayout.setText(font, statusMessage);
        font.draw(batch, statusMessage, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, 50);
    }

    // Instruções
    font.setColor(Color.LIGHT_GRAY);
    font.getData().setScale(1);
    font.draw(batch, "WASD: Mover | ESPAÇO/CLIQUE: Atacar | ESC: Pausar",
        Gdx.graphics.getWidth() / 2 - 200, 20);
    font.getData().setScale(2);
}

private void drawHealthBar(SpriteBatch batch, float x, float y, float width, float height, float percentage, Color color) {
    // Fundo da barra (cinza)
    batch.setColor(Color.GRAY);
    batch.draw(backgroundTextures[0], x, y, width, height);

    // Barra de vida
    batch.setColor(color);
    batch.draw(backgroundTextures[0], x, y, width * percentage, height);

    // Reset da cor
    batch.setColor(Color.WHITE);
}

private void renderPauseOverlay() {
    batch.begin();

    // Desenhar overlay semi-transparente
    batch.draw(pauseOverlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Texto de pausa
    font.setColor(Color.WHITE);
    String pauseText = "JOGO PAUSADO";
    glyphLayout.setText(font, pauseText);
    font.draw(batch, pauseText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 + 50);

    // Instruções
    font.setColor(Color.YELLOW);
    String resumeText = "Pressione ESC para continuar";
    glyphLayout.setText(font, resumeText);
    font.draw(batch, resumeText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2);

    batch.end();
}

private void renderGameOver() {
    batch.begin();

    // Fundo escuro
    batch.setColor(0, 0, 0, 1);
    batch.draw(backgroundTextures[0], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.setColor(Color.WHITE);

    // Imagem de Game Over
    batch.draw(gameOverTexture,
        (Gdx.graphics.getWidth() - gameOverTexture.getWidth()) / 2,
        (Gdx.graphics.getHeight() - gameOverTexture.getHeight()) / 2);

    // Texto de Game Over
    font.setColor(Color.RED);
    String gameOverText = "GAME OVER";
    glyphLayout.setText(font, gameOverText);
    font.draw(batch, gameOverText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 + 100);

    // Estatísticas finais
    font.setColor(Color.YELLOW);
    font.draw(batch, "Inimigos derrotados: " + enemiesKilled,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2);
    font.draw(batch, "Ouro coletado: " + gold,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2 - 30);

    // Instruções para reiniciar
    font.setColor(Color.WHITE);
    String restartText = "Pressione ENTER para reiniciar";
    glyphLayout.setText(font, restartText);
    font.draw(batch, restartText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 - 80);

    batch.end();

    // Verificar se o jogador pressionou ENTER
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
        restartGame();
    }
}

private void restartGame() {
    // Reiniciar variáveis
    playerHealth = MAX_HEALTH;
    enemiesKilled = 0;
    keys = 0;
    gold = 0;
    gameOver = false;

    // Voltar para a sala inicial
    currentRoom = 0;
    setupRoom(currentRoom);

    // Posicionar jogador
    playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
    playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;

    showMessage("Jogo reiniciado!");
}

private void showMessage(String message) {
    statusMessage = message;
    messageTimer = 3.0f;
}

private void updateMessage(float delta) {
    if (messageTimer > 0) {
        messageTimer -= delta;
        if (messageTimer <= 0) {
            statusMessage = "";
        }
    }
}

@Override
public void dispose() {
    batch.dispose();
    font.dispose();

    // Liberar texturas
    if (backgroundTextures != null) {
        for (Texture tex : backgroundTextures) {
            if (tex != null) tex.dispose();
        }
    }

    if (enemyTextures != null) {
        for (Texture tex : enemyTextures) {
            if (tex != null) tex.dispose();
        }
    }

    if (itemTextures != null) {
        for (Texture tex : itemTextures) {
            if (tex != null) tex.dispose();
        }
    }

    if (doorTextures != null) {
        for (Texture tex : doorTextures) {
            if (tex != null) tex.dispose();
        }
    }

    if (playerTexture != null) playerTexture.dispose();
    if (gameOverTexture != null) gameOverTexture.dispose();
    if (pauseOverlayTexture != null) pauseOverlayTexture.dispose();
    if (attackEffectTexture != null) attackEffectTexture.dispose();


    }

}
