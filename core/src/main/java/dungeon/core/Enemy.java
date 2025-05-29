package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.graphics.Texture;  // Importa a classe Texture para carregar imagens dos inimigos
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa SpriteBatch para desenhar texturas no ecrã
import java.util.Random;  // Importa Random para gerar números aleatórios usados no movimento
import dungeon.core.ai.EnemyStrategy;  // Importa a interface de estratégia
import dungeon.core.events.GameEventManager;  // Importa o gestor de eventos

/**
 * Classe que representa um inimigo no jogo.
 * Herda de Character (implementa Herança - um dos 4 pilares da OOP).
 * Usa o padrão Strategy para diferentes comportamentos de IA.
 */
public class Enemy extends Character {

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

        // Métodos getter para aceder às propriedades (necessários para compatibilidade)
        public String getTexturePath() { return texturePath; }
        public int getHealth() { return health; }
        public int getAttackDamage() { return attackDamage; }
        public float getMoveSpeed() { return moveSpeed; }
        public float getAttackCooldown() { return attackCooldown; }
        public int getGoldValue() { return goldValue; }
    }

    // Componente de combate - específico do Enemy
    private float attackCooldown;  // Tempo mínimo entre ataques (em segundos)
    private float lastAttackTime = 0f;  // Controla quando foi o último ataque do inimigo
    private int goldValue;  // Quantidade de ouro que o jogador recebe ao derrotar este inimigo

    // Componente de IA - estratégia de comportamento do inimigo
    private EnemyStrategy strategy;  // Estratégia que define como o inimigo se comporta

    // Ferramentas para movimento aleatório
    private Random random;  // Gerador de números aleatórios para movimentos imprevisíveis

    /**
     * Construtor que inicializa um inimigo com propriedades específicas.
     */
    public Enemy(float x, float y, EnemyType type, EnemyStrategy strategy) {  // Construtor que inicializa um inimigo com propriedades específicas
        // Chama o construtor da classe pai (Character) com os valores do inimigo
        super(x, y, 128f, 128f, type.getHealth(), type.getAttackDamage(),
            type.getMoveSpeed(), type.getTexturePath());  // Posição, dimensões, vida, dano, velocidade e textura

        // Inicializa os atributos específicos do Enemy
        this.attackCooldown = type.getAttackCooldown();  // Define o tempo entre ataques
        this.goldValue = type.getGoldValue();  // Define o valor em ouro

        // Inicializa ferramentas para movimento aleatório
        this.random = new Random();  // Cria o gerador de números aleatórios

        // Define e inicializa a estratégia de comportamento
        this.strategy = strategy;  // Define a estratégia de comportamento
        if (strategy != null) {  // Verifica se a estratégia foi fornecida
            strategy.init(this);  // Inicializa a estratégia com referência a este inimigo
        }
    }

    /**
     * Método abstrato implementado - atualiza o inimigo a cada frame.
     */
    @Override
    public void update(float deltaTime) {  // Método chamado a cada frame para atualizar o estado do inimigo
        lastAttackTime += deltaTime;  // Atualiza o temporizador desde o último ataque

        // Atualiza a hitbox para coincidir com a posição atual
        hitbox.set(x, y, width, height);  // Sincroniza a hitbox com a posição atual do inimigo
    }

    /**
     * Versão específica do update que recebe o Player para IA e combate.
     */
    public void update(float deltaTime, Player player) {  // Método sobrecarregado para atualizar com referência ao jogador
        // Chama o update base
        update(deltaTime);  // Atualiza o estado básico do inimigo

        // Atualiza o comportamento de IA
        updateAI(deltaTime, player);  // Atualiza o movimento com base no tipo de IA

        // Serve para verificar a colisão com o jogador e atacar se possível
        if (hitbox.overlaps(player.getHitbox())) {  // Verifica se o inimigo está a tocar no jogador
            if (canAttack()) {  // Verifica se o cooldown do ataque já passou
                attackPlayer(player);  // Ataca o jogador se ambas as condições forem verdadeiras
            }
        }
    }

    /**
     * Método abstrato implementado - verifica se o inimigo pode atacar.
     */
    @Override
    public boolean canAttack() {  // Método que verifica se o inimigo pode fazer um ataque
        return lastAttackTime >= attackCooldown;  // Devolve verdadeiro se já passou tempo suficiente desde o último ataque
    }

    /**
     * Método que actualiza o comportamento do inimigo usando a estratégia.
     */
    private void updateAI(float deltaTime, Player player) {  // Método que actualiza o comportamento do inimigo usando a estratégia
        if (strategy != null) {  // Verifica se há uma estratégia definida
            strategy.update(this, deltaTime, player);  // Executa a lógica da estratégia
        }
    }

    /**
     * Método que faz o inimigo atacar o jogador.
     */
    private void attackPlayer(Player player) {  // Método que faz o inimigo atacar o jogador
        player.takeDamage(attackDamage);  // Reduz a vida do jogador com base no dano do inimigo
        lastAttackTime = 0;  // Reinicia o temporizador de ataque

        // Notifica o sistema de eventos sobre o ataque
        GameEventManager eventManager = GameEventManager.getInstance();  // Obtém o gestor de eventos
        eventManager.notifyPlayerDamaged(player, attackDamage, this);  // Notifica que o jogador foi atacado
    }

    /**
     * Sobrescreve o método takeDamage da classe pai para adicionar lógica específica.
     */
    @Override
    public void takeDamage(int damage) {  // Método que reduz a vida do inimigo quando ele é atacado
        super.takeDamage(damage);  // Chama o método da classe pai para reduzir a vida

        // Se o inimigo morreu, notifica o sistema de eventos e faz drop de item
        if (isDead()) {  // Se o inimigo morreu
            dropItem();  // Faz o inimigo deixar cair um item
            GameEventManager eventManager = GameEventManager.getInstance();  // Obtém o gestor de eventos
            Player player = DungeonManager.getInstance().getPlayer();  // Obtém referência ao jogador
            eventManager.notifyEnemyDefeated(this, player);  // Notifica que o inimigo foi derrotado
        }
    }

    /**
     * Desenha a barra de vida acima do inimigo.
     */
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

    /**
     * Obtém o valor em ouro que o jogador ganha ao derrotar este inimigo.
     */
    public int getGoldValue() {  // Método que devolve o valor em ouro que o jogador ganha ao derrotar este inimigo
        return goldValue;  // Retorna a quantidade de ouro
    }

    /**
     * Método para mudar a estratégia de IA durante o jogo.
     */
    public void setStrategy(EnemyStrategy newStrategy) {  // Permite trocar o comportamento do inimigo
        this.strategy = newStrategy;  // Define nova estratégia
        if (strategy != null) {  // Se a nova estratégia não for nula
            strategy.init(this);  // Inicializa a nova estratégia
        }
    }

    /**
     * Método que faz o inimigo deixar cair um item ao morrer.
     */
    private void dropItem() {  // Método que faz o inimigo deixar cair um item ao morrer
        float chance = random.nextFloat();  // Gera número entre 0 e 1

        if (chance < 0.4f) {  // 40% de chance de largar poção
            Item droppedItem = new Item(x + width/2, y + height/2, Item.ItemType.HEALTH_POTION);  // Cria poção na posição do inimigo
            Room currentRoom = DungeonManager.getInstance().getCurrentRoom();  // Obtém a sala atual
            currentRoom.addItem(droppedItem);  // Adiciona item à sala
            System.out.println("O inimigo deixou cair uma poção!");  // Informa no console
        } else if (chance < 0.7f) {  // 30% de chance de largar moedas
            Item droppedItem = new Item(x + width/2, y + height/2, Item.ItemType.GOLD_COIN);  // Cria moedas na posição do inimigo
            Room currentRoom = DungeonManager.getInstance().getCurrentRoom();  // Obtém a sala atual
            currentRoom.addItem(droppedItem);  // Adiciona item à sala
            System.out.println("O inimigo deixou cair moedas de ouro!");  // Informa no console
        } else if (chance < 0.8f) {  // 10% de chance de largar chave
            Item droppedItem = new Item(x + width/2, y + height/2, Item.ItemType.KEY);  // Cria chave na posição do inimigo
            Room currentRoom = DungeonManager.getInstance().getCurrentRoom();  // Obtém a sala atual
            currentRoom.addItem(droppedItem);  // Adiciona item à sala
            System.out.println("O inimigo deixou cair uma chave!");  // Informa no console
        }
        // 20% de chance de não largar nada
    }
}
