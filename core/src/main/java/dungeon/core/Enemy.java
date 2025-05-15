package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.graphics.Texture;  // Importa a classe Texture para carregar imagens dos inimigos
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa SpriteBatch para desenhar texturas no ecrã
import com.badlogic.gdx.math.Rectangle;  // Importa Rectangle para criar hitboxes e detetar colisões
import com.badlogic.gdx.math.Vector2;  // Importa Vector2 para representar posições e direções 2D
import java.util.Random;  // Importa Random para gerar números aleatórios usados no movimento


public class Enemy {
    // Tipos de inimigos disponíveis no jogo
    public enum EnemyType {  // Enumeração que define os diferentes tipos de inimigos e suas características
        ZOMBIE("enemy.png", 50, 5, 100f, 0.8f, 30),  // Zombie: imagem, vida, dano, velocidade, tempo entre ataques, valor em ouro
        SKELETON("skeleton.png", 40, 8, 120f, 0.6f, 40),  // Esqueleto: mais rápido, menos vida, mais dano que o Zombie
        BOSS("boss.png", 200, 15, 80f, 1.5f, 100);  // Boss: muito mais vida e dano, mais lento, dá mais ouro quando derrotado

        private final String texturePath;  // Caminho para o ficheiro de imagem do inimigo
        private final int health;  // Quantidade de vida do inimigo
        private final int attackDamage;  // Quantidade de dano que o inimigo causa ao jogador
        private final float moveSpeed;  // Velocidade de movimento do inimigo
        private final float attackCooldown;  // Tempo de espera entre ataques (em segundos)
        private final int goldValue;  // Quantidade de ouro que o jogador recebe ao derrotar o inimigo

        EnemyType(String texturePath, int health, int attackDamage, float moveSpeed,
                  float attackCooldown, int goldValue) {  // Construtor que define as características de cada tipo
            this.texturePath = texturePath;  // Define o caminho da imagem
            this.health = health;  // Define a vida inicial
            this.attackDamage = attackDamage;  // Define o dano de ataque
            this.moveSpeed = moveSpeed;  // Define a velocidade de movimento
            this.attackCooldown = attackCooldown;  // Define o intervalo entre ataques
            this.goldValue = goldValue;  // Define o valor em ouro quando derrotado
        }
    }

    // Componente de posição - posicionamento do(s) inimigo(s) no ecrã
    private float x, y;  // Coordenadas X e Y do inimigo no mundo do jogo
    private float width = 128f;  // Largura do inimigo em pixels
    private float height = 128f;  // Altura do inimigo em pixels
    private float moveSpeed;  // Velocidade de movimento (pixels por segundo)
    private Rectangle hitbox;  // Área para detetar colisões com o jogador

    // Componente visual - aparência do inimigo
    private Texture texture;  // Imagem que representa o inimigo no ecrã

    // Componente de estatísticas - valores que definem as capacidades do inimigo
    private int health;  // Vida atual do inimigo
    private int maxHealth;  // Vida máxima do inimigo
    private int attackDamage;  // Quantidade de dano que o inimigo causa ao jogador por ataque
    private float attackCooldown;  // Tempo mínimo entre ataques (em segundos)
    private float lastAttackTime = 0f;  // Controla quando foi o último ataque do inimigo
    private int goldValue;  // Quantidade de ouro que o jogador recebe ao derrotar este inimigo

    // Componente de IA - comportamento do inimigo
    private AIType aiType;  // Tipo de comportamento do inimigo (seguir, aleatório, patrulha)
    private Vector2 targetPosition;  // Posição alvo para onde o inimigo está a mover-se
    private float timeSinceLastDirectionChange = 0f;  // Tempo desde a última mudança de direção
    private float directionChangeInterval = 2f;  // Intervalo entre mudanças de direção (em segundos)
    private Random random;  // Gerador de números aleatórios para movimentos imprevisíveis

    // Tipos de IA disponíveis para os inimigos
    public enum AIType {  // Enumeração que define os diferentes comportamentos de IA
        BASIC_FOLLOWER,    // Segue o jogador diretamente
        RANDOM_MOVEMENT,   // Movimento aleatório pelo mapa
        PATROL_AREA        // Patrulha uma área definida
    }

    public Enemy(float x, float y, EnemyType type, AIType aiType) {  // Construtor que inicializa um inimigo com propriedades específicas
        		
		// Define a posição inicial do inimigo
		this.x = x;  // Posição X inicial
		this.y = y;  // Posição Y inicial
    
		// Copia as características do tipo de inimigo selecionado
		this.texture = new Texture(type.texturePath);  // Carrega a imagem do inimigo
		this.health = type.health;  // Define a vida atual
		this.maxHealth = type.health;  // Define a vida máxima igual à vida inicial
		this.attackDamage = type.attackDamage;  // Define o dano de ataque
		this.moveSpeed = type.moveSpeed;  // Define a velocidade de movimento
		this.attackCooldown = type.attackCooldown;  // Define o tempo entre ataques
		this.goldValue = type.goldValue;  // Define o valor em ouro
    
		// Define o comportamento e inicializa componentes de colisão
		this.aiType = aiType;  // Define o tipo de IA/comportamento
		this.hitbox = new Rectangle(x, y, width, height);  // Cria a hitbox para detetar colisões
    
		// Inicializa ferramentas para movimento aleatório
		this.random = new Random();  // Cria o gerador de números aleatórios
		this.targetPosition = new Vector2(x, y);  // Define a posição inicial como alvo inicial
	}

    public void update(float deltaTime, Player player) {  // Método chamado a cada frame para atualizar o estado do inimigo
        lastAttackTime += deltaTime;  // Atualiza o temporizador desde o último ataque

        // Atualiza o comportamento de IA
        updateAI(deltaTime, player);  // Atualiza o movimento com base no tipo de IA

        // Atualiza a hitbox para coincidir com a posição atual
        hitbox.set(x, y, width, height);  // Sincroniza a hitbox com a posição atual do inimigo

        // Serve para verificar a colisão com o jogador e atacar se possível
        if (hitbox.overlaps(player.getHitbox())) {  // Verifica se o inimigo está a tocar no jogador
            if (lastAttackTime >= attackCooldown) {  // Verifica se o cooldown do ataque já passou
                attackPlayer(player);  // Ataca o jogador se ambas as condições forem verdadeiras
            }
        }
    }

    private void updateAI(float deltaTime, Player player) {  // Método que atualiza o comportamento do inimigo com base no seu tipo de IA
        switch (aiType) {  // Verifica qual é o tipo de IA deste inimigo
            case BASIC_FOLLOWER:  // Comportamento de perseguidor básico
                // Segue o jogador diretamente
                moveTowardsPlayer(player, deltaTime);  // Move o inimigo em direção ao jogador
                break;

            case RANDOM_MOVEMENT:  // Comportamento de movimento aleatório
                // Movimento aleatório
                timeSinceLastDirectionChange += deltaTime;  // Atualiza o temporizador de mudança de direção
                if (timeSinceLastDirectionChange >= directionChangeInterval) {  // Verifica se é hora de mudar de direção
                    // Escolher uma nova direção aleatória
                    targetPosition.x = random.nextFloat() * (800 - width);  // Escolhe uma coordenada X aleatória dentro dos limites do ecrã
                    targetPosition.y = random.nextFloat() * (600 - height);  // Escolhe uma coordenada Y aleatória dentro dos limites do ecrã
                    timeSinceLastDirectionChange = 0;  // Reinicia o temporizador
                }
                moveTowardsTarget(targetPosition, deltaTime);  // Move o inimigo em direção ao ponto aleatório
                break;

            case PATROL_AREA:  // Comportamento de patrulha
                // Patrulha uma área definida
                timeSinceLastDirectionChange += deltaTime;  // Atualiza o temporizador de mudança de ponto de patrulha
                if (timeSinceLastDirectionChange >= directionChangeInterval) {  // Verifica se é hora de mudar de ponto de patrulha
                    // Escolher um novo ponto de patrulha
                    float centerX = 400;  // Centro X da área de patrulha
                    float centerY = 300;  // Centro Y da área de patrulha
                    float radius = 200;  // Raio da área de patrulha
                    float angle = random.nextFloat() * 360;  // Ângulo aleatório (0-360 graus)
                    targetPosition.x = centerX + (float) Math.cos(Math.toRadians(angle)) * radius;  // Calcula X usando trigonometria
                    targetPosition.y = centerY + (float) Math.sin(Math.toRadians(angle)) * radius;  // Calcula Y usando trigonometria
                    timeSinceLastDirectionChange = 0;  // Reinicia o temporizador
                }
                moveTowardsTarget(targetPosition, deltaTime);  // Move o inimigo em direção ao ponto de patrulha

                // Se o jogador estiver próximo, muda para estado de perseguição
                float distanceToPlayer = Vector2.dst(x, y, player.getX(), player.getY());  // Calcula a distância até o jogador
                if (distanceToPlayer < 200) {  // Se o jogador estiver a menos de 200 pixels
                    moveTowardsPlayer(player, deltaTime);  // Abandona a patrulha e persegue o jogador
                }
                break;
        }
    }

    private void moveTowardsPlayer(Player player, float deltaTime) {  // Método que move o inimigo em direção ao jogador
        // Calcula a direção para o jogador
        float directionX = player.getX() - x;  // Componente X do vetor direção
        float directionY = player.getY() - y;  // Componente Y do vetor direção
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);  // Comprimento do vetor direção

        // Normaliza a direção
        if (length > 0) {  // Evita divisão por zero
            directionX /= length;  // Normaliza o componente X do vetor (valor entre -1 e 1)
            directionY /= length;  // Normaliza o componente Y do vetor (valor entre -1 e 1)
        }

        // Move em direção ao jogador
        x += directionX * moveSpeed * deltaTime;  // Atualiza a posição X
        y += directionY * moveSpeed * deltaTime;  // Atualiza a posição Y

        // Limita os movimentos do inimigo para dentro da tela
        x = Math.max(0, Math.min(x, 800 - width));  // Mantém o inimigo dentro dos limites horizontais
        y = Math.max(0, Math.min(y, 600 - height));  // Mantém o inimigo dentro dos limites verticais
    }

    private void moveTowardsTarget(Vector2 target, float deltaTime) {  // Método que move o inimigo em direção a um ponto específico
        // Calcular direção para o alvo
        float directionX = target.x - x;  // Componente X do vetor direção
        float directionY = target.y - y;  // Componente Y do vetor direção
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);  // Comprimento do vetor direção

        // Interrompe o movimento quando está suficientemente perto do destino
        if (length < 5) {  // Se a distância até o alvo for menor que 5 pixels
            return;  // Interrompe o movimento
        }

        // Normaliza a direção
        directionX /= length;  // Normaliza o componente X do vetor (valor entre -1 e 1)
        directionY /= length;  // Normaliza o componente Y do vetor (valor entre -1 e 1)

        // Deslocamento no sentido do alvo
        x += directionX * moveSpeed * deltaTime;  // Atualiza a posição X
        y += directionY * moveSpeed * deltaTime;  // Atualiza a posição Y

        // Limitar os movimentos do inimigo para dentro da tela
        x = Math.max(0, Math.min(x, 800 - width));  // Mantém o inimigo dentro dos limites horizontais
        y = Math.max(0, Math.min(y, 600 - height));  // Mantém o inimigo dentro dos limites verticais
    }

    private void attackPlayer(Player player) {  // Método que faz o inimigo atacar o jogador
        player.takeDamage(attackDamage);  // Reduz a vida do jogador com base no dano do inimigo
        lastAttackTime = 0;  // Reinicia o temporizador de ataque
    }

    public void takeDamage(int damage) {  // Método que reduz a vida do inimigo quando ele é atacado
        health -= damage;  // Subtrai o dano da vida atual
        if (health < 0) health = 0;  // Impede que a vida fique negativa
    }

    public void render(SpriteBatch batch) {  // Método que desenha o inimigo no ecrã
        batch.draw(texture, x, y, width, height);  // Desenha a textura do inimigo na sua posição atual
    }

    public void drawHealthBar(SpriteBatch batch, Texture barTexture) {  // Método que desenha a barra de vida acima do inimigo
        float healthBarWidth = width;  // Largura da barra de vida igual à largura do inimigo
        float healthBarHeight = 10f;  // Altura da barra de vida em pixels
        float healthPercentage = (float) health / maxHealth;  // Calcula a percentagem de vida restante

        // Barra de fundo (cinza)
        batch.setColor(0.3f, 0.3f, 0.3f, 1);  // Define a cor cinza
        batch.draw(barTexture, x, y + height + 5, healthBarWidth, healthBarHeight);  // Desenha o fundo da barra de vida

        // Barra de saúde (vermelho)
        batch.setColor(1, 0, 0, 1);  // Define a cor vermelha
        batch.draw(barTexture, x, y + height + 5, healthBarWidth * healthPercentage, healthBarHeight);  // Desenha a parte colorida da barra

        // Configuração da cor para o valor padrão
        batch.setColor(1, 1, 1, 1);  // Retorna a cor para branco (normal)
    }

    public float getX() {  // Método que devolve a coordenada X do inimigo
        return x;  // Retorna a posição X atual
    }

    public float getY() {  // Método que devolve a coordenada Y do inimigo
        return y;  // Retorna a posição Y atual
    }

    public float getWidth() {  // Método que devolve a largura do inimigo
        return width;  // Retorna a largura em pixels
    }

    public float getHeight() {  // Método que devolve a altura do inimigo
        return height;  // Retorna a altura em pixels
    }

    public int getHealth() {  // Método que devolve a vida atual do inimigo
        return health;  // Retorna o valor atual da vida
    }

    public int getMaxHealth() {  // Método que devolve a vida máxima do inimigo
        return maxHealth;  // Retorna o valor máximo da vida
    }

    public boolean isDead() {  // Método que verifica se o inimigo está morto
        return health <= 0;  // Retorna verdadeiro se a vida for zero ou menor
    }

    public int getGoldValue() {  // Método que devolve o valor em ouro que o jogador ganha ao derrotar este inimigo
        return goldValue;  // Retorna a quantidade de ouro
    }

    public Rectangle getHitbox() {  // Método que devolve a hitbox do inimigo para verificação de colisões
        return hitbox;  // Retorna o retângulo de colisão
    }

    public void dispose() {  // Método para libertar os recursos utilizados pelo inimigo
        texture.dispose();  // Liberta a memória usada pela textura do inimigo
    }
}
