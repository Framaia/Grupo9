package dungeon.core; // Define o pacote onde esta classe está localizada.

// Importações de bibliotecas do LibGDX e Java
import com.badlogic.gdx.ScreenAdapter;  // Classe base para telas no LibGDX
import com.badlogic.gdx.Gdx;  // Acesso a funcionalidades globais do LibGDX
import com.badlogic.gdx.graphics.Color;  // Para trabalhar com cores (ainda não usado)
import com.badlogic.gdx.graphics.Texture;  // Representa uma imagem carregada
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Utilizado para desenhar gráficos 2D
import com.badlogic.gdx.graphics.g2d.BitmapFont;  // Permite desenhar textos na tela
import com.badlogic.gdx.graphics.g2d.GlyphLayout;  // Usado para calcular o tamanho do texto renderizado
import com.badlogic.gdx.utils.ScreenUtils;  // Métodos utilitários relacionados à tela (ex: limpar)
import com.badlogic.gdx.Input;  // Permite detectar entradas de teclado, mouse, etc.
import com.badlogic.gdx.graphics.Pixmap;  // Permite manipulação de imagens em memória
import com.badlogic.gdx.graphics.Pixmap.Format;  // Define o formato de cor de um Pixmap
import java.util.ArrayList;  // Lista dinâmica da Java API
import java.util.List;  // Interface de lista
import java.util.Random;  // Geração de números aleatórios
import com.badlogic.gdx.audio.Sound;  // Representa um efeito sonoro curto
import com.badlogic.gdx.audio.Music;  // Representa música de fundo ou longa duração



public class GameplayScreen extends ScreenAdapter {  // Define a classe GameplayScreen, que representa a tela principal do jogo. Ela herda de ScreenAdapter, o que permite usar os métodos render(), show(), hide(), etc.

    // Referência ao jogo principal
    private MainGame game;

    // Elementos de renderização
       private SpriteBatch batch;  // Objeto usado para desenhar imagens na tela
    private BitmapFont font;  // Fonte para desenhar texto
    private GlyphLayout glyphLayout;  // Usado para medir e alinhar texto renderizado


    // Estado do jogo
        private boolean paused = false;  // Indica se o jogo está pausado
    private boolean gameOver = false;  // Indica se o jogador perdeu
    private boolean gameWon = false;  // Indica se o jogador venceu (em teste)
    private String statusMessage = "";  // Mensagem de status (ex: "Você venceu!")
    private float messageTimer = 0;  // Temporizador para exibir a mensagem


    // Sala atual
      private int currentRoom = 0;  // Índice da sala atual
    private final int TOTAL_ROOMS = 4;  // Quantidade total de salas no jogo


    // Imagens
     private Texture[] backgroundTextures;  // Fundos das salas
    private Texture playerTexture;  // Textura do personagem principal
    private Texture[] enemyTextures;  // Diferentes texturas de inimigos
    private Texture gameOverTexture;  // Imagem exibida quando o jogador perde
    private Texture pauseOverlayTexture;  // Imagem semi-transparente para quando o jogo está pausado
    private Texture[] itemTextures;  // Texturas dos diferentes itens no jogo
    private Texture attackEffectTexture;  // Efeito gráfico de ataque
    private Texture[] doorTextures;  // Texturas das portas nas salas


    // Jogador
       private float playerX, playerY;  // Posição atual do jogador na tela
    private float playerWidth = 192f;  // Largura do sprite do jogador
    private float playerHeight = 192f;  // Altura do sprite do jogador
    private float moveSpeed = 300f;  // Velocidade de movimento do jogador (pixels por segundo)
    private int playerHealth = 100;  // Vida atual do jogador
    private final int MAX_HEALTH = 100;  // Vida máxima que o jogador pode ter
    private int keys = 0;  // Quantidade de chaves coletadas
    private int gold = 0;  // Quantidade de ouro coletado

    // Sons do jogo
      private Sound attackSound;  // Som tocado ao atacar
    private Sound hitSound;  // Som tocado ao receber dano
    private Music victoryMusic;  // Música tocada ao vencer



    // Inimigos
       private List<Enemy> enemies = new ArrayList<>();  // Lista dos inimigos ativos na sala
    private int enemiesKilled = 0;  // Contador de inimigos derrotados


    // Itens
       private List<Item> items = new ArrayList<>();  // Itens disponíveis na sala atual


    // Portas
       private List<Door> doors = new ArrayList<>();  // Portas conectando as salas

    // Efeitos visuais
       private boolean showAttackEffect = false;  // Indica se o efeito de ataque deve ser mostrado
    private float attackEffectX, attackEffectY;  // Posição do efeito de ataque
    private float attackEffectTimer = 0;  // Temporizador para exibir o efeito por tempo limitado

    // Utilitários
        private Random random = new Random();  // Gerador de números aleatórios


    // Classe para representar os inimigos
       private class Enemy {
        float x, y;  // Posição do inimigo
        float width, height;  // Tamanho do inimigo
        float speed;  // Velocidade de movimentação
        int health;  // Vida atual do inimigo
        int maxHealth;  // Vida máxima
        int damage;  // Dano que ele causa ao jogador
        String type;  // Tipo (ex: "arqueiro", "guerreiro")
        Texture texture;  // Textura do inimigo
        boolean isDead = false;  // Se o inimigo está morto


            public Enemy(float x, float y, String type, Texture texture, int health, float speed, int damage) {
            this.x = x;  // Define a posição X do inimigo
            this.y = y;  // Define a posição Y do inimigo
            this.type = type;  // Tipo do inimigo (ex: arqueiro, guerreiro)
            this.texture = texture;  // Textura que representa o inimigo
            this.health = health;  // Vida inicial
            this.maxHealth = health;  // Vida máxima (inicialmente igual à atual)
            this.speed = speed;  // Velocidade de movimentação
            this.damage = damage;  // Dano causado ao jogador
            this.width = 192;  // Largura fixa do inimigo
            this.height = 192;  // Altura fixa do inimigo
        }

               public void update(float delta) {
            if (isDead) return;  // Se estiver morto, não faz nada

            // Calcula a direção até o jogador
            float dirX = playerX - x;
            float dirY = playerY - y;

            // Normaliza o vetor direção para ter magnitude 1
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                dirX /= length;
                dirY /= length;
            }

            // Move o inimigo na direção do jogador, proporcional ao tempo decorrido
            x += dirX * speed * delta;
            y += dirY * speed * delta;
        }


        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
            return !isDead &&  // Não verifica colisão se estiver morto
                px < x + width * 0.7f && px + pWidth > x + width * 0.3f &&  // Verifica sobreposição horizontal
                py < y + height * 0.7f && py + pHeight > y + height * 0.3f;  // Verifica sobreposição vertical
        }


              public void takeDamage(int amount) {
            if (isDead) return;  // Ignora se já estiver morto

            health -= amount;  // Subtrai dano da vida
            if (health <= 0) {  // Se a vida zerar ou ficar negativa
                health = 0;
                isDead = true;
                showMessage("Inimigo " + type + " derrotado!");  // Mostra mensagem ao jogador

                // Faz o inimigo deixar cair um item ao morrer
                dropItem();
            }
        }

               private void dropItem() {
            float chance = random.nextFloat();  // Gera número entre 0 e 1

            if (chance < 0.4f) {  // 40% de chance de largar poção
                items.add(new Item(x + width/2, y + height/2, "health_potion", itemTextures[0], 20));
                showMessage("O inimigo deixou cair uma poção!");
            } else if (chance < 0.7f) {  // 30% de chance de largar moedas
                items.add(new Item(x + width/2, y + height/2, "gold_coin", itemTextures[2], 10));
                showMessage("O inimigo deixou cair moedas de ouro!");
            } else if (chance < 0.8f) {  // 10% de chance de largar chave
                items.add(new Item(x + width/2, y + height/2, "key", itemTextures[1], 1));
                showMessage("O inimigo deixou cair uma chave! Apanha-a!!");
            }
        }

    }

    // Classe para representar os itens
        private class Item {
        float x, y;  // Posição do item
        float width, height;  // Tamanho do item
        Texture texture;  // Imagem que representa o item
        String type;  // Tipo do item (ex: "health_potion", "gold_coin")
        int value;  // Valor do item (vida, ouro, etc.)
        boolean collected = false;  // Se já foi apanhado
        float bobTimer = 0;  // Usado para animação de flutuação

               public Item(float x, float y, String type, Texture texture, int value) {
            this.x = x; // Define a posição X do item
            this.y = y; // Define a posição Y do item
            this.type = type;  // Tipo do item
            this.texture = texture;  // Textura do item
            this.value = value;  // Valor que ele dá ao jogador
            this.width = 48;  // Tamanho fixo
            this.height = 48;
        }
                   // Atualiza a animação do item a cada frame
                public void update(float delta) {
            if (collected) return;  // Se já foi apanhado, não atualiza

                  // Animação de flutuação
        bobTimer += delta;  // Incrementa o tempo para controlar o movimento de flutuação
        y += Math.sin(bobTimer * 5) * 0.5f;  // Altera levemente a posição vertical 


        }
        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
            return !collected &&  // Só verifica se o item ainda não foi apanhado
                px < x + width && px + pWidth > x &&  // Verifica colisão horizontal
                py < y + height && py + pHeight > y;  // Verifica colisão vertical
        }

    }

    // Classe para representar as portas para mudar de sala
       private class Door {
        float x, y;  // Posição da porta
        float width, height;  // Tamanho da porta
        Texture texture;  // Imagem da porta
        String direction;  // Direção da porta (ex: "north")
        int targetRoom;  // Sala de destino ao passar pela porta
        boolean isLocked;  // Indica se a porta está trancada


         // Construtor da porta
    public Door(float x, float y, String direction, Texture texture, int targetRoom, boolean isLocked) {
        this.x = x;  // Define posição X
        this.y = y;  // Define posição Y
        this.direction = direction;  // Define a direção
        this.texture = texture;  // Define a imagem
        this.targetRoom = targetRoom;  // Define a sala de destino
        this.isLocked = isLocked;  // Define se está trancada

         // Define o tamanho da porta com base na direção
            if (direction.equals("north") || direction.equals("south")) {
                this.width = 192; // Portas horizontais (topo/baixo), mais largas e mais baixas
                this.height = 96;
            } else {
                this.width = 96;  // Portas verticais (laterais), mais estreitas e mais altas
                this.height = 192;
            }
        }
                      // Verifica colisão entre a porta e outro objeto (ex: jogador)
        public boolean isColliding(float px, float py, float pWidth, float pHeight) {
              return px < x + width && px + pWidth > x &&  // Colisão horizontal
               py < y + height && py + pHeight > y;  // Colisão vertical
    }
    }
// Construtor da tela de jogo que recebe a instância principal do jogo
public GameplayScreen(MainGame game) {
    this.game = game; // Armazena a referência ao jogo principal
}

// Método chamado quando esta tela é exibida pela primeira vez
@Override
public void show() {
    batch = new SpriteBatch(); // Inicializa o objeto responsável por desenhar sprites
    font = new BitmapFont(); // Cria uma nova fonte padrão
    font.getData().setScale(2); // Aumenta o tamanho da fonte
    glyphLayout = new GlyphLayout(); // Inicializa o layout usado para medir texto

    // Carrega todas as texturas necessárias para o jogo
    loadTextures();

    // Define a posição inicial do jogador no centro da tela
    playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
    playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;

    // Carrega os sons do jogo a partir de arquivos
    attackSound = Gdx.audio.newSound(Gdx.files.internal("sword_slash.wav")); // Som de ataque
    hitSound = Gdx.audio.newSound(Gdx.files.internal("damage_sound.wav"));   // Som de dano
    victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("victory_sound1.wav")); // Música de vitória
    victoryMusic.setLooping(false); // Define para não repetir a música de vitória

    // Configura a primeira sala do jogo
    setupRoom(currentRoom);
}

// Método responsável por carregar todas as texturas do jogo
private void loadTextures() {
    // Inicializa o array de texturas de fundo para as salas
    backgroundTextures = new Texture[TOTAL_ROOMS];
    
    // Carrega a textura de fundo de cada sala (ou cria uma textura colorida de fallback)
    for (int i = 0; i < TOTAL_ROOMS; i++) {
        String fileName = (i == 0) ? "background.jpg" : "background" + (i+1) + ".jpg"; // Nome do ficheiro
        backgroundTextures[i] = loadTextureOrCreate(fileName, 800, 600, createColor(0.2f + i*0.1f, 0.2f, 0.3f - i*0.05f, 1));
    }

    // Inicializa e carrega as texturas dos inimigos: zumbi, esqueleto e chefe
    enemyTextures = new Texture[3];
    enemyTextures[0] = loadTextureOrCreate("enemy.png", 128, 128, Color.RED); // Zumbi
    enemyTextures[1] = loadTextureOrCreate("skeleton.png", 128, 192, Color.LIGHT_GRAY); // Esqueleto
    enemyTextures[2] = loadTextureOrCreate("boss.png", 128, 128, Color.PURPLE); // Chefe (boss)

    // Inicializa e carrega as texturas dos itens: poção, chave e moeda
    itemTextures = new Texture[3];
    itemTextures[0] = loadTextureOrCreate("health_potion.png", 32, 32, Color.GREEN); // Poção de vida
    itemTextures[1] = loadTextureOrCreate("key.png", 32, 32, Color.YELLOW);           // Chave
    itemTextures[2] = loadTextureOrCreate("gold_coin.png", 32, 32, Color.GOLD);       // Moeda de ouro

    // Inicializa e carrega as texturas das portas nas direções e trancada
    doorTextures = new Texture[5];
    doorTextures[0] = loadTextureOrCreate("door_north.png", 128, 64, Color.BROWN); // Porta norte
    doorTextures[1] = loadTextureOrCreate("door_south.png", 128, 64, Color.BROWN); // Porta sul
    doorTextures[2] = loadTextureOrCreate("door_east.png", 64, 128, Color.BROWN);  // Porta leste
    doorTextures[3] = loadTextureOrCreate("door_west.png", 64, 128, Color.BROWN);  // Porta oeste
    doorTextures[4] = loadTextureOrCreate("door_locked.png", 128, 64, Color.GRAY); // Porta trancada

    // Carrega outras texturas auxiliares do jogo
    playerTexture = loadTextureOrCreate("player.png", 128, 128, Color.BLUE);          // Textura do jogador
    gameOverTexture = loadTextureOrCreate("game_over.jpg", 400, 300, Color.BLACK);    // Tela de game over
    pauseOverlayTexture = createColorTexture(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, 0, 0, 0.7f)); // Overlay de pausa
    attackEffectTexture = loadTextureOrCreate("attack_effect.png", 64, 64, Color.YELLOW); // Efeito visual de ataque
}

// Método que tenta carregar uma textura do disco, ou cria uma textura de fallback colorida
private Texture loadTextureOrCreate(String path, int width, int height, Color color) {
    try {
        // Verifica se o ficheiro de textura existe
        if (Gdx.files.internal(path).exists()) {
            return new Texture(Gdx.files.internal(path)); // Carrega e retorna a textura real
        }
    } catch (Exception e) {
        // Mostra uma mensagem de erro se falhar ao carregar a textura
        System.err.println("Erro ao carregar textura: " + path + " - " + e.getMessage());
    }

    // Caso o ficheiro não exista, cria uma textura de cor sólida como fallback
    return createColorTexture(width, height, color);
}

// Cria uma textura colorida com as dimensões e cor especificadas
private Texture createColorTexture(int width, int height, Color color) {
    Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888); // Cria um pixmap (imagem em memória)
    pixmap.setColor(color); // Define a cor para desenhar

       pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // Liberta a memória do Pixmap após criar a textura
        return texture; // Devolve a textura criada com a cor definida
    }

 // Cria uma nova cor com os valores RGBA fornecidos
    private Color createColor(float r, float g, float b, float a) {
        return new Color(r, g, b, a);
    }
     // Configura a sala com base no ID fornecido
    private void setupRoom(int roomId) {
        // Limpa as listas de inimigos, itens e portas da sala anterior
        enemies.clear();
        items.clear();
        doors.clear();
            // Obtém as dimensões do ecrã para posicionamento
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Define o conteúdo de cada sala com base no ID
        switch (roomId) {
            case 0: // Sala inicial
                // Adiciona um inimigo do lado direito
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.5f, "zombie", enemyTextures[0], 50, 60, 5));

                // Adiciona os itens disponíveis na sala
                items.add(new Item(200, 200, "health_potion", itemTextures[0], 20));
                items.add(new Item(400, 400, "gold_coin", itemTextures[2], 10));

                // Adiciona portas de saída da sala
                doors.add(new Door(screenWidth/2 - 96, screenHeight - 32, "north", doorTextures[0], 1, false));
                doors.add(new Door(screenWidth - 32, screenHeight/2 - 96, "east", doorTextures[2], 2, false));
                break;

             case 1: // Sala a norte
                // Adiciona inimigos do lado direito
                enemies.add(new Enemy(screenWidth * 0.7f, screenHeight * 0.6f, "skeleton", enemyTextures[1], 70, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.3f, "zombie", enemyTextures[0], 50, 60, 5));

                // Adiciona item (chave)
                items.add(new Item(300, 400, "key", itemTextures[1], 1));

                // Adiciona porta de retorno
                doors.add(new Door(screenWidth/2 - 96, 32, "south", doorTextures[1], 0, false));
                break;

          case 2: // Sala a leste
                // Adiciona inimigos
                enemies.add(new Enemy(screenWidth * 0.75f, screenHeight * 0.7f, "skeleton", enemyTextures[1], 70, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.85f, screenHeight * 0.4f, "skeleton", enemyTextures[1], 70, 60, 8));

                // Adiciona portas
                doors.add(new Door(32, screenHeight/2 - 96, "west", doorTextures[3], 0, false));
                doors.add(new Door(screenWidth - 32, screenHeight/2 - 96, "east", doorTextures[2], 3, true));
                break;

            case 3: // Sala secreta (Boss)
                // Adiciona o Boss no centro-direita
                enemies.add(new Enemy(screenWidth * 0.7f, screenHeight * 0.5f, "boss", enemyTextures[2], 400, 60, 20));

                // Adiciona guardas do boss
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.3f, "skeleton", enemyTextures[1], 100, 60, 10));
                enemies.add(new Enemy(screenWidth * 0.8f, screenHeight * 0.7f, "skeleton", enemyTextures[1], 100, 60, 10));
                enemies.add(new Enemy(screenWidth * 0.6f, screenHeight * 0.2f, "zombie", enemyTextures[0], 80, 60, 8));
                enemies.add(new Enemy(screenWidth * 0.6f, screenHeight * 0.8f, "zombie", enemyTextures[0], 80, 60, 8));

                // Adiciona itens (tesouro)
                items.add(new Item(screenWidth * 0.6f, screenHeight * 0.3f, "health_potion", itemTextures[0], 50));
                items.add(new Item(screenWidth * 0.8f, screenHeight * 0.3f, "gold_coin", itemTextures[2], 100));

                // Adiciona porta de retorno
                doors.add(new Door(32, screenHeight/2 - 96, "west", doorTextures[3], 2, false));
                break;
        }

    }

  @Override
    public void render(float delta) {
        // Limpa o ecrã
        ScreenUtils.clear(0, 0, 0, 1);

        // Verifica se o jogador venceu o jogo
        if (gameWon) {
            renderVictory(); // Mostra o ecrã de vitória
            return;
        }

        // Verifica se o jogador pausou o jogo
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                showMessage("Jogo Pausado - Pressione ESC para continuar");
            } else {
                showMessage("Jogo Continuado");
            }
        }

         // Verifica se o jogador perdeu
        if (gameOver) {
            renderGameOver(); // Mostra o ecrã de fim de jogo
            return;
        }

       // Se o jogo não estiver pausado, actualiza a lógica
        if (!paused) {
            update(delta);
        }

 // Renderiza os elementos do jogo (jogador, inimigos, itens, portas, etc.)
        renderGame();

        // Se o jogo estiver pausado, mostra um overlay
        if (paused) {
            renderPauseOverlay();
        }
    }

   private void update(float delta) {
    // Atualiza a mensagem de status na tela (duração, etc.)
    updateMessage(delta);

    // Se o efeito de ataque estiver ativo, diminui o tempo restante
    if (showAttackEffect) {
        attackEffectTimer -= delta;
        if (attackEffectTimer <= 0) {
            showAttackEffect = false; // Esconde o efeito quando o tempo acaba
        }
    }

    // Verifica se a vida do jogador acabou
    if (playerHealth <= 0) {
        gameOver = true; // Ativa o estado de fim de jogo
        return; // Sai da função para não processar mais nada
    }

    // Lida com entrada do jogador (movimento e ataque)
    handleInput(delta);

    // Atualiza todos os inimigos
    for (Enemy enemy : enemies) {
        enemy.update(delta); // Atualiza lógica do inimigo

        // Se colidir com o jogador, causa dano
        if (enemy.isColliding(playerX, playerY, playerWidth, playerHeight)) {
            playerHealth -= enemy.damage * delta; // Dano depende do tempo
            if (playerHealth < 0) playerHealth = 0; // Vida não pode ser negativa
        }
    }

    // Atualiza os itens no mapa
    for (Item item : items) {
        item.update(delta); // Pode ser animado, por exemplo

        // Se o jogador colidir com o item, coleta ele
        if (item.isColliding(playerX, playerY, playerWidth, playerHeight)) {
            collectItem(item);
        }
    }

    // Verifica colisão com portas
    for (Door door : doors) {
        if (door.isColliding(playerX, playerY, playerWidth, playerHeight)) {
            if (door.isLocked && keys > 0) {
                door.isLocked = false; // Destranca a porta
                keys--; // Consome uma chave
                showMessage("Porta desbloqueada! Chaves restantes: " + keys);
            } else if (!door.isLocked) {
                changeRoom(door.targetRoom); // Vai para a nova sala
                return; // Sai do update após mudar de sala
            } else {
                // Porta trancada e sem chave
                showMessage("Esta porta está trancada. Você precisa de uma chave!");
            }
        }
    }

    // Condição de vitória: sala 3 e todos inimigos mortos
    if (!gameWon && currentRoom == 3 && enemies.stream().allMatch(e -> e.isDead)) {
        gameWon = true;
        victoryMusic.play(); // Toca música de vitória
        showMessage("Derrotaste o Boss! Vitória!");
    }
}
// Método que lida com a entrada do jogador (movimento e ataque)
private void handleInput(float delta) {
    // Movimento horizontal para a esquerda (seta esquerda ou tecla A)
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
        playerX -= moveSpeed * delta; // Move o jogador para a esquerda com base na velocidade e no tempo
    }

    // Movimento horizontal para a direita (seta direita ou tecla D)
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
        playerX += moveSpeed * delta; // Move o jogador para a direita
    }

    // Movimento vertical para cima (seta para cima ou tecla W)
    if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
        playerY += moveSpeed * delta; // Move o jogador para cima
    }

    // Movimento vertical para baixo (seta para baixo ou tecla S)
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
        playerY -= moveSpeed * delta; // Move o jogador para baixo
    }

    // Garante que o jogador não saia da tela (limites horizontais)
    playerX = Math.max(0, Math.min(playerX, Gdx.graphics.getWidth() - playerWidth));
    // Garante que o jogador não saia da tela (limites verticais)
    playerY = Math.max(0, Math.min(playerY, Gdx.graphics.getHeight() - playerHeight));

    // Verifica se o jogador pressionou espaço ou clicou com o botão esquerdo do mouse para atacar
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        attack(); // Executa o ataque
    }
}

   // Método que executa a lógica de ataque do jogador
private void attack() {
    boolean hitEnemy = false; // Flag para verificar se algum inimigo foi atingido

    // Percorre todos os inimigos
    for (Enemy enemy : enemies) {
        // Verifica se o inimigo está vivo e se está perto o suficiente
        if (!enemy.isDead && isNearby(playerX, playerY, enemy.x, enemy.y, 200)) {
            // Exibe o efeito visual do ataque
            showAttackEffect = true;
            attackEffectTimer = 0.3f; // Duração do efeito visual
            attackEffectX = enemy.x; // Posição X do efeito
            attackEffectY = enemy.y; // Posição Y do efeito

            // Aplica dano ao inimigo
            enemy.takeDamage(20);
            hitSound.play(); // Toca som de acerto
            hitEnemy = true; // Marca que um inimigo foi atingido

            // Se o inimigo morreu após o ataque, aumenta o contador
            if (enemy.isDead) {
                enemiesKilled++;
            }
        }
    }
    // Se nenhum inimigo foi atingido, toca som diferente e exibe mensagem
    if (!hitEnemy) {
        attackSound.play();
        showMessage("Atacaste, mas não acertaste em nenhum inimigo!");
    }
}

  // Método auxiliar que verifica se dois pontos estão a uma certa distância
private boolean isNearby(float x1, float y1, float x2, float y2, float distance) {
    // Calcula a distância euclidiana entre os pontos e compara com a distância máxima
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) <= distance;
}


// Método que lida com a coleta de itens
private void collectItem(Item item) {
    if (item.collected) return; // Se o item já foi coletado, não faz nada

    item.collected = true; // Marca o item como coletado

    // Verifica o tipo do item coletado
    switch (item.type) {
        case "health_potion":
            // Recupera vida até o limite máximo
            playerHealth = Math.min(playerHealth + item.value, MAX_HEALTH);
            showMessage("Recuperaste " + item.value + " pontos de vida");
            break;

        case "key":
            // Adiciona chaves ao inventário
            keys += item.value;
            showMessage("Encontraste uma chave! Total: " + keys);
            break;

        case "gold_coin":
            // Adiciona moedas de ouro
            gold += item.value;
            showMessage("Conseguiste " + item.value + " moedas de ouro");
            break;
    }
}
// Método que muda o jogador para uma nova sala
private void changeRoom(int newRoom) {
    // Verifica se o número da sala é válido
    if (newRoom < 0 || newRoom >= TOTAL_ROOMS) return;

    currentRoom = newRoom; // Atualiza a sala atual
    setupRoom(currentRoom); // Configura a nova sala

    // Centraliza o jogador na tela
    playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
    playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;
}

     // Método que desenha todos os elementos do jogo na tela
    private void renderGame() {
        batch.begin();    // Começa o processo de desenho na tela

        // Desenha o fundo da sala atual
        batch.draw(backgroundTextures[currentRoom], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

       // Desenha as portas da sala atual
for (Door door : doors) {
    // Usa uma textura diferente se a porta estiver trancada
    Texture doorTex = door.isLocked ? doorTextures[4] : door.texture;
    // Desenha a porta na posição e tamanho definidos
    batch.draw(doorTex, door.x, door.y, door.width, door.height);
}

       // Desenha os itens espalhados pela sala
for (Item item : items) {
    // Só desenha se o item ainda não foi coletado
    if (!item.collected) {
        batch.draw(item.texture, item.x, item.y, item.width, item.height);
    }
}

      // Desenha os inimigos da sala
for (Enemy enemy : enemies) {
    // Só desenha se o inimigo ainda estiver vivo
    if (!enemy.isDead) {
        batch.draw(enemy.texture, enemy.x, enemy.y, enemy.width, enemy.height);
        // Desenha a barra de vida do inimigo acima dele
        drawHealthBar(batch, enemy.x, enemy.y + enemy.height + 10, enemy.width, 10,
            (float)enemy.health / enemy.maxHealth, Color.RED);
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


     // Finaliza o desenho da cena
        batch.end();

    }

  private void drawUI() {
    // Desenha a barra de vida do jogador
    drawHealthBar(batch, 10, Gdx.graphics.getHeight() - 30, 200, 20,
        (float)playerHealth / MAX_HEALTH, Color.GREEN);

    // Define a cor da fonte para branco
    font.setColor(Color.WHITE);
    
    // Mostra informações do jogador no canto superior esquerdo
    font.draw(batch, "Vida: " + playerHealth + "/" + MAX_HEALTH, 10, Gdx.graphics.getHeight() - 40);
    font.draw(batch, "Sala: " + (currentRoom + 1) + "/" + TOTAL_ROOMS, 10, Gdx.graphics.getHeight() - 70);
    font.draw(batch, "Chaves: " + keys, 10, Gdx.graphics.getHeight() - 100);
    font.draw(batch, "Ouro: " + gold, 10, Gdx.graphics.getHeight() - 130);
    font.draw(batch, "Inimigos derrotados: " + enemiesKilled, 10, Gdx.graphics.getHeight() - 160);

    // Mostra mensagem de status (ex: "Você encontrou uma chave")
    if (!statusMessage.isEmpty()) {
        font.setColor(Color.YELLOW);
        glyphLayout.setText(font, statusMessage); // Calcula o tamanho do texto
        font.draw(batch, statusMessage, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, 50);
    }

    // Mostra instruções básicas do jogo na parte inferior
    font.setColor(Color.LIGHT_GRAY);
    font.getData().setScale(1); // Reduz temporariamente o tamanho da fonte
    font.draw(batch, "WASD: Mover | ESPAÇO/CLIQUE: Atacar | ESC: Pausar",
        Gdx.graphics.getWidth() / 2 - 200, 20);
    font.getData().setScale(2); // Restaura o tamanho da fonte
}


    private void drawHealthBar(SpriteBatch batch, float x, float y, float width, float height, float percentage, Color color) {
    // Define cor cinza para o fundo da barra
    batch.setColor(Color.GRAY);
    batch.draw(backgroundTextures[0], x, y, width, height);

    // Define cor da barra conforme parâmetro (vermelho ou verde)
    batch.setColor(color);
    batch.draw(backgroundTextures[0], x, y, width * percentage, height);

    // Restaura a cor padrão (evita afetar outros desenhos)
    batch.setColor(Color.WHITE);
}


    private void renderPauseOverlay() {
    batch.begin(); // Começa a desenhar o overlay

    // Desenha uma textura semi-transparente cobrindo toda a tela
    batch.draw(pauseOverlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Escreve "JOGO PAUSADO" no centro da tela
    font.setColor(Color.WHITE);
    String pauseText = "JOGO PAUSADO";
    glyphLayout.setText(font, pauseText); // Mede o texto
    font.draw(batch, pauseText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 + 50);

    // Instruções para continuar o jogo
    font.setColor(Color.YELLOW);
    String resumeText = "Pressione ESC para continuar";
    glyphLayout.setText(font, resumeText);
    font.draw(batch, resumeText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2);

    batch.end(); // Finaliza o desenho
}


    private void renderGameOver() {
    batch.begin();

    // Preenche o fundo com uma textura escura
    batch.setColor(0, 0, 0, 1); // Cor preta com opacidade total
    batch.draw(backgroundTextures[0], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.setColor(Color.WHITE); // Restaura a cor original para os próximos elementos

    // Desenha a imagem "Game Over" centralizada
    batch.draw(gameOverTexture,
        (Gdx.graphics.getWidth() - gameOverTexture.getWidth()) / 2,
        (Gdx.graphics.getHeight() - gameOverTexture.getHeight()) / 2);

    // Mostra estatísticas da partida (quantos inimigos foram derrotados, quanto ouro foi coletado)
    font.setColor(Color.WHITE);
    font.draw(batch, "Inimigos derrotados: " + enemiesKilled,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2 - 150);
    font.draw(batch, "Ouro coletado: " + gold,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2 - 180);

    // Instrução para reiniciar o jogo
    String restartText = "Pressione ENTER para reiniciar";
    glyphLayout.setText(font, restartText);
    font.draw(batch, restartText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 - 80);

    batch.end();

    // Reinicia o jogo se o jogador pressionar ENTER
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
        restartGame();
    }
}

  private void renderVictory() {
    batch.begin();

    // Fundo claro (branco) para dar tom de celebração
    batch.setColor(1, 1, 1, 1);
    batch.draw(backgroundTextures[0], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.setColor(Color.WHITE);

    // Mensagem de vitória centralizada
    font.setColor(Color.GOLD); // Dourado para destaque
    String victoryText = "Parabéns! Derrotaste o Boss!";
    glyphLayout.setText(font, victoryText);
    font.draw(batch, victoryText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 + 100);

    // Estatísticas da vitória
    font.setColor(Color.WHITE);
    font.draw(batch, "Inimigos derrotados: " + enemiesKilled,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2 - 50);
    font.draw(batch, "Ouro coletado: " + gold,
        (Gdx.graphics.getWidth() - 200) / 2,
        Gdx.graphics.getHeight() / 2 - 80);

    // Instrução para jogar novamente
    font.setColor(Color.YELLOW);
    String restartText = "Pressione ENTER para jogar de novo";
    glyphLayout.setText(font, restartText);
    font.draw(batch, restartText,
        (Gdx.graphics.getWidth() - glyphLayout.width) / 2,
        Gdx.graphics.getHeight() / 2 - 150);

    // Efeitos festivos (confetes simples)
    for (int i = 0; i < 50; i++) {
        float x = random.nextInt(Gdx.graphics.getWidth());
        float y = random.nextInt(Gdx.graphics.getHeight());
        font.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));
        font.draw(batch, "*", x, y); // Desenha símbolos como confete
    }

    batch.end();

    // Reinicia o jogo se o jogador pressionar ENTER
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
        setupRoom(currentRoom);  // Recria a sala e seus elementos (inimigos, itens, etc.)

        // Posicionar jogador
        playerX = (Gdx.graphics.getWidth() - playerWidth) / 2f;
        playerY = (Gdx.graphics.getHeight() - playerHeight) / 2f;  

        showMessage("Jogo reiniciado!");
    }

    private void showMessage(String message) {
    statusMessage = message;   // Armazena a mensagem a ser exibida
    messageTimer = 3.0f;       // Define o tempo (em segundos) que a mensagem ficará visível
}

  private void updateMessage(float delta) {
    if (messageTimer > 0) {
        messageTimer -= delta;           // Reduz o tempo restante com base no tempo do frame
        if (messageTimer <= 0) {
            statusMessage = "";          // Limpa a mensagem quando o tempo acaba
        }
    }
}

   @Override
public void dispose() {
    batch.dispose();  //  o SpriteBatch
    font.dispose();   //  a fonte usada nos textos

    //  as texturas de fundo
    if (backgroundTextures != null) {
        for (Texture tex : backgroundTextures) {
            if (tex != null) tex.dispose();
        }
    }

    //  texturas dos inimigos
    if (enemyTextures != null) {
        for (Texture tex : enemyTextures) {
            if (tex != null) tex.dispose();
        }
    }

    //  texturas dos itens
    if (itemTextures != null) {
        for (Texture tex : itemTextures) {
            if (tex != null) tex.dispose();
        }
    }

    // texturas das portas
    if (doorTextures != null) {
        for (Texture tex : doorTextures) {
            if (tex != null) tex.dispose();
        }
    }

    // texturas específicas
    if (playerTexture != null) playerTexture.dispose();
    if (gameOverTexture != null) gameOverTexture.dispose();
    if (pauseOverlayTexture != null) pauseOverlayTexture.dispose();
    if (attackEffectTexture != null) attackEffectTexture.dispose();

    // sons e músicas
    attackSound.dispose();
    hitSound.dispose();
    victoryMusic.dispose();
}
}