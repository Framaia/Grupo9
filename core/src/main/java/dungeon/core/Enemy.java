package dungeon.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/* 
  Classe Enemy - Representa os inimigos que o jogador encontra no dungeon.
  Implementa vários tipos de inimigos com diferentes comportamentos, seguindo o padrão Entity-Component que separa posição, aparência, estatísticas e comportamento
*/
public class Enemy {
    // Tipos de inimigos disponíveis no jogo
    public enum EnemyType {
        ZOMBIE("enemy.png", 50, 5, 100f, 0.8f, 30),
        SKELETON("skeleton.png", 40, 8, 120f, 0.6f, 40),
        BOSS("boss.png", 200, 15, 80f, 1.5f, 100);

        private final String texturePath;
        private final int health;
        private final int attackDamage;
        private final float moveSpeed;
        private final float attackCooldown;
        private final int goldValue;

        EnemyType(String texturePath, int health, int attackDamage, float moveSpeed,
                  float attackCooldown, int goldValue) {
            this.texturePath = texturePath;
            this.health = health;
            this.attackDamage = attackDamage;
            this.moveSpeed = moveSpeed;
            this.attackCooldown = attackCooldown;
            this.goldValue = goldValue;
        }
    }

    // Componente de posição - posicionamento do(s) inimigo(s) no ecrã
    private float x, y;
    private float width = 128f;
    private float height = 128f;
    private float moveSpeed;
    private Rectangle hitbox;  // Área para detetar colisões com o jogador

    // Componente visual - aparência do inimigo
    private Texture texture;

    // Componente de estatísticas - valores que definem as capacidades do inimigo
    private int health;
    private int maxHealth;
    private int attackDamage;
    private float attackCooldown;
    private float lastAttackTime = 0f;
    private int goldValue;

    // Componente de IA - comportamento do inimigo
    private AIType aiType;
    private Vector2 targetPosition;
    private float timeSinceLastDirectionChange = 0f;
    private float directionChangeInterval = 2f;
    private Random random;

    // Tipos de IA disponíveis para os inimigos
    public enum AIType {
        BASIC_FOLLOWER,    // Segue o jogador diretamente
        RANDOM_MOVEMENT,   // Movimento aleatório pelo mapa
        PATROL_AREA        // Patrulha uma área definida
    }

    /* 
      Construtor da classe Enemy.
      
      Cria um inimigo com as características definidas pelo seu tipo (zombie, skeleton, boss)
      e o comportamento definido pelo tipo de IA escolhido.
    */
    public Enemy(float x, float y, EnemyType type, AIType aiType) {
        		
		// Define a posição inicial do inimigo
		this.x = x;
		this.y = y;
    
		// Copia as características do tipo de inimigo selecionado
		this.texture = new Texture(type.texturePath);
		this.health = type.health;
		this.maxHealth = type.health;
		this.attackDamage = type.attackDamage;
		this.moveSpeed = type.moveSpeed;
		this.attackCooldown = type.attackCooldown;
		this.goldValue = type.goldValue;
    
		// Define o comportamento e inicializa componentes de colisão
		this.aiType = aiType;
		this.hitbox = new Rectangle(x, y, width, height);
    
		// Inicializa ferramentas para movimento aleatório
		this.random = new Random();
		this.targetPosition = new Vector2(x, y);
		}

    /* 
      Atualiza o estado do inimigo a cada frame.
      Controla o movimento, ataques e colisões com o jogador.
      O parâmetro deltaTime representa o tempo desde o último frame, permitindo que o movimento seja independente da velocidade do computador.
    */
    public void update(float deltaTime, Player player) {
        lastAttackTime += deltaTime;

        // Atualiza o comportamento de IA
        updateAI(deltaTime, player);

        // Atualiza a hitbox para coincidir com a posição atual
        hitbox.set(x, y, width, height);

        // Serve para verificar a colisão com o jogador e atacar se possível
        if (hitbox.overlaps(player.getHitbox())) {
            if (lastAttackTime >= attackCooldown) {
                attackPlayer(player);
            }
        }
    }

    /* 
      Atualiza o comportamento da IA do inimigo.
      O comportamento varia dependendo do tipo de IA:
      - BASIC_FOLLOWER: persegue o jogador diretamente
      - RANDOM_MOVEMENT: move-se aleatoriamente pelo mapa
      - PATROL_AREA: patrulha uma área específica, perseguindo o jogador se estiver próximo
    */
    private void updateAI(float deltaTime, Player player) {
        switch (aiType) {
            case BASIC_FOLLOWER:
                // Segue o jogador diretamente
                moveTowardsPlayer(player, deltaTime);
                break;

            case RANDOM_MOVEMENT:
                // Movimento aleatório
                timeSinceLastDirectionChange += deltaTime;
                if (timeSinceLastDirectionChange >= directionChangeInterval) {
                    // Escolher uma nova direção aleatória
                    targetPosition.x = random.nextFloat() * (800 - width);
                    targetPosition.y = random.nextFloat() * (600 - height);
                    timeSinceLastDirectionChange = 0;
                }
                moveTowardsTarget(targetPosition, deltaTime);
                break;

            case PATROL_AREA:
                // Patrulha uma área definida
                timeSinceLastDirectionChange += deltaTime;
                if (timeSinceLastDirectionChange >= directionChangeInterval) {
                    // Escolher um novo ponto de patrulha
                    float centerX = 400;
                    float centerY = 300;
                    float radius = 200;
                    float angle = random.nextFloat() * 360;
                    targetPosition.x = centerX + (float) Math.cos(Math.toRadians(angle)) * radius;
                    targetPosition.y = centerY + (float) Math.sin(Math.toRadians(angle)) * radius;
                    timeSinceLastDirectionChange = 0;
                }
                moveTowardsTarget(targetPosition, deltaTime);

                // Se o jogador estiver próximo, muda para perseguição
                float distanceToPlayer = Vector2.dst(x, y, player.getX(), player.getY());
                if (distanceToPlayer < 200) {
                    moveTowardsPlayer(player, deltaTime);
                }
                break;
        }
    }

    /* 
      Move o inimigo em direção ao jogador.
      Calcula a direção para o jogador e desloca o inimigo nessa direção, considerando a sua velocidade de movimento.
    */
    private void moveTowardsPlayer(Player player, float deltaTime) {
        // Calcula a direção para o jogador
        float directionX = player.getX() - x;
        float directionY = player.getY() - y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Normaliza a direção
        if (length > 0) {
            directionX /= length;
            directionY /= length;
        }

        // Move em direção ao jogador
        x += directionX * moveSpeed * deltaTime;
        y += directionY * moveSpeed * deltaTime;

        // Limitar os movimentos do inimigo para dentro da tela
        x = Math.max(0, Math.min(x, 800 - width));
        y = Math.max(0, Math.min(y, 600 - height));
    }

    /* 
      Move o inimigo em direção a um ponto específico.
      Usado principalmente pelos tipos de IA RANDOM_MOVEMENT e PATROL_AREA.
    */
    private void moveTowardsTarget(Vector2 target, float deltaTime) {
        // Calcular direção para o alvo
        float directionX = target.x - x;
        float directionY = target.y - y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Interrompe o movimento quando está suficientemente perto do destino
        if (length < 5) {
            return;
        }

        // Normaliza a direção
        directionX /= length;
        directionY /= length;

        // Deslocamento no sentido do alvo
        x += directionX * moveSpeed * deltaTime;
        y += directionY * moveSpeed * deltaTime;

        // Limitar os movimentos do inimigo para dentro da tela
        x = Math.max(0, Math.min(x, 800 - width));
        y = Math.max(0, Math.min(y, 600 - height));
    }

    /* 
      Ataca o jogador, causando dano baseado no attackDamage do inimigo.
      Reinicia o temporizador de ataque para controlar a frequência.
    */
    private void attackPlayer(Player player) {
        player.takeDamage(attackDamage);
        lastAttackTime = 0;
    }

    /* 
      Reduz a vida do inimigo quando ele sofre dano.
      Se a vida chegar a zero, o inimigo é considerado morto.
    */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    /* 
      Apresentação visual do inimigo no ecrã.
	  Utiliza a textura associada ao tipo de inimigo.
    */
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    /* 
      Visualização da barra de vida do inimigo.
      Barra colorida posicionada acima do inimigo que diminui com os danos sofridos.
    */
    public void drawHealthBar(SpriteBatch batch, Texture barTexture) {
        float healthBarWidth = width;
        float healthBarHeight = 10f;
        float healthPercentage = (float) health / maxHealth;

        // Barra de fundo (cinza)
        batch.setColor(0.3f, 0.3f, 0.3f, 1);
        batch.draw(barTexture, x, y + height + 5, healthBarWidth, healthBarHeight);

        // Barra de saúde (vermelho)
        batch.setColor(1, 0, 0, 1);
        batch.draw(barTexture, x, y + height + 5, healthBarWidth * healthPercentage, healthBarHeight);

        // Configuração da cor para o valor padrão
        batch.setColor(1, 1, 1, 1);
    }

    /* 
      Devolve a coordenada X do inimigo.
    */
    public float getX() {
        return x;
    }

    /* 
      Devolve a coordenada Y do inimigo.
    */
    public float getY() {
        return y;
    }

    /* 
      Devolve a largura do inimigo.
      Usada para cálculos de posicionamento e colisão.
    */
    public float getWidth() {
        return width;
    }

    /* 
      Devolve a altura do inimigo.
      Usada para cálculos de posicionamento e colisão.
    */
    public float getHeight() {
        return height;
    }

    /* 
      Devolve a vida atual do inimigo.
    */
    public int getHealth() {
        return health;
    }

    /* 
      Devolve a vida máxima do inimigo.
      Útil para calcular a percentagem de vida para a barra de saúde.
    */
    public int getMaxHealth() {
        return maxHealth;
    }

    /* 
      Verifica se o inimigo está morto (vida = 0).
    */
    public boolean isDead() {
        return health <= 0;
    }

    /* 
      Devolve o valor em ouro que o jogador ganha ao derrotar este inimigo.
    */
    public int getGoldValue() {
        return goldValue;
    }

    /* 
      Devolve a hitbox do inimigo para verificação de colisões.
    */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /* 
      Gestão dos recursos gráficos usados pelo inimigo.
      Importante chamar quando o inimigo não é mais necessário, para evitar memory leaks.
    */
    public void dispose() {
        texture.dispose();
    }
}
