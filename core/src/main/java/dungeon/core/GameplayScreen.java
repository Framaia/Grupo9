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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;


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
    private boolean gameWon = false;  // EM TESTE
    private String statusMessage = "";
    private float messageTimer = 0;

    // Sala atual
    private int currentRoom = 0;
    private final int TOTAL_ROOMS = 4;

    // Imagens
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
    private float playerWidth = 192f;
    private float playerHeight = 192f;
    private float moveSpeed = 300f;
    private int playerHealth = 100;
    private final int MAX_HEALTH = 100;
    private int keys = 0;
    private int gold = 0;

    // Sons do jogo
    private Sound attackSound;
    private Sound hitSound;
    private Music victoryMusic;


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

    // Classe para representar os inimigos
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
            this.width = 192;
            this.height = 192;
        }

        public void update(float delta) {
            if (isDead) return;

            // Movimento em direção ao jogador
            float dirX = playerX - x;
            float dirY = playerY - y;

            // Normalizar a direção
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                dirX /= length;
                dirY /= length;
            }

            // Mover o inimigo
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

                // Deixar cair o Item
                dropItem();
            }
        }

        private void dropItem() {
            float chance = random.nextFloat();

            if (chance < 0.4f) { // 40% poção
                items.add(new Item(x + width/2, y + height/2, "health_potion", itemTextures[0], 20));
                showMessage("Aviso: O inimigo deixou cair uma poção!");
            } else if (chance < 0.7f) { // 30% moeda
                items.add(new Item(x + width/2, y + height/2, "gold_coin", itemTextures[2], 10));
                showMessage("Aviso: O inimigo deixou cair moedas de ouro!");
            } else if (chance < 0.8f) { // 10% chave
                items.add(new Item(x + width/2, y + height/2, "key", itemTextures[1], 1));
                showMessage("Aviso: O inimigo deixou cair uma chave! Apanhe-a!!");
            }
        }
    }

    // Classe para representar os itens
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
            this.width = 48;
            this.height = 48;
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

    // Classe para representar as portas para mudar de sala
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

            // Ajustar as dimensões com base na direção
            if (direction.equals("north") || direction.equals("south")) {
                this.width = 192;
                this.height = 96;
            } else {
                this.width = 96;
                this.height = 192;
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
        // Carregar sons
        attackSound = Gdx.audio.newSound(Gdx.files.internal("sword_slash.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("damage_sound.wav"));
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("victory_sound1.wav"));
        victoryMusic.setLooping(false);

        // Configurar a sala inicial
        setupRoom(currentRoom);

    }

    private void loadTextures() {
        // Imagem de fundo para cada sala
        backgroundTextures = new Texture[TOTAL_ROOMS];
        for (int i = 0; i < TOTAL_ROOMS; i++) {
            String fileName = (i == 0) ? "background.jpg" : "background" + (i+1) + ".jpg";
            backgroundTextures[i] = loadTextureOrCreate(fileName, 800, 600, createColor(0.2f + i*0.1f, 0.2f, 0.3f - i*0.05f, 1));
        }

        // Imagens de inimigos
        enemyTextures = new Texture[3]; // zumbi, esqueleto, boss
        enemyTextures[0] = loadTextureOrCreate("enemy.png", 128, 128, Color.RED);
        enemyTextures[1] = loadTextureOrCreate("skeleton.png", 128, 192, Color.LIGHT_GRAY);
        enemyTextures[2] = loadTextureOrCreate("boss.png", 128, 128, Color.PURPLE);

        // Imagens de itens
        itemTextures = new Texture[3]; // poção, chave, moeda
        itemTextures[0] = loadTextureOrCreate("health_potion.png", 32, 32, Color.GREEN);
        itemTextures[1] = loadTextureOrCreate("key.png", 32, 32, Color.YELLOW);
        itemTextures[2] = loadTextureOrCreate("gold_coin.png", 32, 32, Color.GOLD);

        // Imagens de portas
        doorTextures = new Texture[5]; // norte, sul, leste, oeste, trancada
        doorTextures[0] = loadTextureOrCreate("door_north.png", 128, 64, Color.BROWN);
        doorTextures[1] = loadTextureOrCreate("door_south.png", 128, 64, Color.BROWN);
        doorTextures[2] = loadTextureOrCreate("door_east.png", 64, 128, Color.BROWN);
        doorTextures[3] = loadTextureOrCreate("door_west.png", 64, 128, Color.BROWN);
        doorTextures[4] = loadTextureOrCreate("door_locked.png", 128, 64, Color.GRAY);

        // Outras imagens
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
        // Limpar as listas
        enemies.clear();
        items.clear();
        doors.clear();

        // Obter as dimensões do ecrã para posicionamento
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Configuração específica para cada sala
        switch (roomId) {
            case 0: // Sala inicial
                // Posicionar os inimigos do lado direito
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.5f, "zombie", enemyTextures[0], 50, 60, 5));

                // Adicionar os itens
                items.add(new Item(200, 200, "health_potion", itemTextures[0], 20));
                items.add(new Item(400, 400, "gold_coin", itemTextures[2], 10));

                // Adicionar as portas
                doors.add(new Door(screenWidth/2 - 96, screenHeight - 32, "north", doorTextures[0], 1, false));
                doors.add(new Door(screenWidth - 32, screenHeight/2 - 96, "east", doorTextures[2], 2, false));
                break;

            case 1: // Sala norte
                // Inimigos do lado direito
                enemies.add(new Enemy(screenWidth * 0.7f, screenHeight * 0.6f, "skeleton", enemyTextures[1], 70, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.3f, "zombie", enemyTextures[0], 50, 60, 5));

                // Adicionar os itens
                items.add(new Item(300, 400, "key", itemTextures[1], 1));

                // Adicionar as portas
                doors.add(new Door(screenWidth/2 - 96, 32, "south", doorTextures[1], 0, false));
                break;

            case 2: // Sala leste
                // Inimigos do lado direito
                enemies.add(new Enemy(screenWidth * 0.75f, screenHeight * 0.7f, "skeleton", enemyTextures[1], 70, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.85f, screenHeight * 0.4f, "skeleton", enemyTextures[1], 70, 60, 8));

                // Adicionar as portas
                doors.add(new Door(32, screenHeight/2 - 96, "west", doorTextures[3], 0, false));
                doors.add(new Door(screenWidth - 32, screenHeight/2 - 96, "east", doorTextures[2], 3, true));
                break;

            case 3: // Sala secreta (sala do Boss)
                // Boss no centro-direita
                enemies.add(new Enemy(screenWidth * 0.7f, screenHeight * 0.5f, "boss", enemyTextures[2], 400, 60, 20));

                // Adicionar esqueletos - guardas do boss
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.3f, "skeleton", enemyTextures[1], 100, 60, 10));
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.7f, "skeleton", enemyTextures[1], 100, 60, 10));
                enemies.add(new Enemy(screenWidth * 0.6f, screenHeight * 0.2f, "zombie", enemyTextures[0], 80, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.6f, screenHeight * 0.8f, "zombie", enemyTextures[0], 80, 60, 8));

                // Adicionar itens (tesouro)
                items.add(new Item(screenWidth * 0.6f, screenHeight * 0.3f, "health_potion", itemTextures[0], 50));
                items.add(new Item(screenWidth * 0.8f, screenHeight * 0.3f, "gold_coin", itemTextures[2], 100));

                // Adicionar a porta de saída
                doors.add(new Door(32, screenHeight/2 - 96, "west", doorTextures[3], 2, false));
                break;
        }

        // Mensagem de nova sala
        showMessage("Sala " + (roomId + 1));
    }

    @Override
    public void render(float delta) {
        // Limpar o ecrã
        ScreenUtils.clear(0, 0, 0, 1);

        if (gameWon) {
            renderVictory();
            return;
        }


        // Pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                showMessage("Jogo Pausado - Pressione ESC para continuar");
            } else {
                showMessage("Jogo Continuado");
            }
        }

        // Caso esteja em "Game Over" mostra a imagem de fim de jogo
        if (gameOver) {
            renderGameOver();
            return;
        }

        // Se não estiver em pausa, atualiza a lógica do jogo
        if (!paused) {
            update(delta);
        }

        // Renderiza os elementos do jogo
        renderGame();

        // Se estiver em pausa, renderiza o overlay de pausa
        if (paused) {
            renderPauseOverlay();
        }
    }

    private void update(float delta) {
        // Atualiza a mensagem de status
        updateMessage(delta);

        // Atualiza o efeito de ataque
        if (showAttackEffect) {
            attackEffectTimer -= delta;
            if (attackEffectTimer <= 0) {
                showAttackEffect = false;
            }
        }

        // Verifica se o jogador está morto
        if (playerHealth <= 0) {
            gameOver = true;
            return;
        }

        // Processa a entrada do jogador
        handleInput(delta);

        // Atualiza os inimigos
        for (Enemy enemy : enemies) {
            enemy.update(delta);

            // Verifica a colisão dos inimigos com o jogador
            if (enemy.isColliding(playerX, playerY, playerWidth, playerHeight)) {
                playerHealth -= enemy.damage * delta;
                if (playerHealth < 0) playerHealth = 0;
            }
        }

        // Atualiza os itens
        for (Item item : items) {
            item.update(delta);

            // Verifica a colisão com o jogador
            if (item.isColliding(playerX, playerY, playerWidth, playerHeight)) {
                collectItem(item);
            }
        }

        // Verificaa a colisão com portas
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
        // Verificar condição de vitória
        if (!gameWon && currentRoom == 3 && enemies.stream().allMatch(e -> e.isDead)) {
            gameWon = true;
            victoryMusic.play();
            showMessage("Derrotaste o Boss! Vitória!");
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

        // Verifica a colisão com inimigos
        for (Enemy enemy : enemies) {
            if (!enemy.isDead && isNearby(playerX, playerY, enemy.x, enemy.y, 200)) {
                // Mostrar efeito visual
                showAttackEffect = true;
                attackEffectTimer = 0.3f;
                attackEffectX = enemy.x;
                attackEffectY = enemy.y;

                // Causar dano
                enemy.takeDamage(20);
                hitSound.play();
                hitEnemy = true;

                // Contabilizar inimigo morto
                if (enemy.isDead) {
                    enemiesKilled++;
                }
            }
        }

        if (!hitEnemy) {
            attackSound.play();
            showMessage("Atacaste, mas não acertaste em nenhum inimigo!");
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

        // Ajusta a posição do jogador com base na sala anterior
        playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
        playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;
    }

    private void renderGame() {
        batch.begin();

        // Desenha o fundo da sala atual
        batch.draw(backgroundTextures[currentRoom], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Desenha as portas
        for (Door door : doors) {
            Texture doorTex = door.isLocked ? doorTextures[4] : door.texture;
            batch.draw(doorTex, door.x, door.y, door.width, door.height);
        }

        // Desenha os itens
        for (Item item : items) {
            if (!item.collected) {
                batch.draw(item.texture, item.x, item.y, item.width, item.height);
            }
        }

        // Desenha os inimigos
        for (Enemy enemy : enemies) {
            if (!enemy.isDead) {
                batch.draw(enemy.texture, enemy.x, enemy.y, enemy.width, enemy.height);
                drawHealthBar(batch, enemy.x, enemy.y + enemy.height + 10, enemy.width, 10, (float)enemy.health / enemy.maxHealth, Color.RED);
            }
        }

        // Desenha o jogador
        batch.draw(playerTexture, playerX, playerY, playerWidth, playerHeight);

        // Desenha o efeito de ataque
        if (showAttackEffect) {
            batch.draw(attackEffectTexture, attackEffectX, attackEffectY, 128, 128);
        }

        // Desenha a interface
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

        // Estatísticas finais
        font.setColor(Color.WHITE);
        font.draw(batch, "Inimigos derrotados: " + enemiesKilled,
            (Gdx.graphics.getWidth() - 200) / 2,
            Gdx.graphics.getHeight() / 2 - 150);
        font.draw(batch, "Ouro coletado: " + gold,
            (Gdx.graphics.getWidth() - 200) / 2,
            Gdx.graphics.getHeight() / 2 - 180);

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
    private void renderVictory() {
        batch.begin();

        // Fundo festivo
        batch.setColor(1, 1, 1, 1); // Branco total
        batch.draw(backgroundTextures[0], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        // Texto de vitória
        font.setColor(Color.GOLD);
        String victoryText = "Parabéns! Derrotaste o Boss!";
        glyphLayout.setText(font, victoryText);
        font.draw(batch, victoryText,
            (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
            Gdx.graphics.getHeight() / 2 + 100);

        // Estatísticas
        font.setColor(Color.WHITE);
        font.draw(batch, "Inimigos derrotados: " + enemiesKilled,
            (Gdx.graphics.getWidth() - 200) / 2,
            Gdx.graphics.getHeight() / 2 - 50);
        font.draw(batch, "Ouro coletado: " + gold,
            (Gdx.graphics.getWidth() - 200) / 2,
            Gdx.graphics.getHeight() / 2 - 80);

        // Instruções
        font.setColor(Color.YELLOW);
        String restartText = "Pressione ENTER para jogar de novo";
        glyphLayout.setText(font, restartText);
        font.draw(batch, restartText,
            (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
            Gdx.graphics.getHeight() / 2 - 150);

        // Foguetes ou confetes (representação simples com círculos coloridos)
        for (int i = 0; i < 50; i++) {
            float x = random.nextInt(Gdx.graphics.getWidth());
            float y = random.nextInt(Gdx.graphics.getHeight());
            font.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));
            font.draw(batch, "*", x, y); // Símbolo simples como efeito
        }

        batch.end();

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
        gameWon = false; // ← Essencial para reiniciar o jogo

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

        // Apresentar imagens
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
        attackSound.dispose();
        hitSound.dispose();
        victoryMusic.dispose();


    }

}
