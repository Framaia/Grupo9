package dungeon.core;  // Define o pacote onde esta classe está incluída

import com.badlogic.gdx.graphics.Texture;  // Importa a classe Texture da libGDX para carregar e gerir as imagens
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Importa a classe SpriteBatch para desenhar as texturas no ecrã
import com.badlogic.gdx.math.Rectangle;  // Importa a classe Rectangle para criar hitboxes e detetar colisões

/**
 * Classe abstrata que representa uma personagem do jogo.
 * Implementa o princípio da Herança - um dos 4 pilares da OOP.
 * Define atributos e comportamentos comuns para Player e Enemy.
 */
public abstract class Character implements GameEntity {
    // Posição da personagem no mundo do jogo
    protected float x, y;  // Coordenadas da personagem no ecrã

    // Dimensões da personagem
    protected float width, height;  // Largura e altura da personagem em pixels

    // Atributos de vida da personagem
    protected int health;  // Vida atual da personagem
    protected int maxHealth;  // Vida máxima que a personagem pode ter

    // Atributos de combate
    protected int attackDamage;  // Quantidade de dano que a personagem causa

    // Velocidade de movimento
    protected float moveSpeed;  // Velocidade com que a personagem se move pelo ecrã

    // Recursos gráficos
    protected Texture texture;  // Textura para desenhar a personagem no ecrã

    // Sistema de colisões
    protected Rectangle hitbox;  // Área para detetar colisões com outras entidades

    /**
     * Construtor base para todas as personagens.
     * Só pode ser chamado pelas subclasses (Player e Enemy).
     */
    protected Character(float x, float y, float width, float height,
                        int maxHealth, int attackDamage, float moveSpeed,
                        String texturePath) {  // Construtor que inicializa uma personagem com as suas propriedades básicas
        // Define a posição inicial da personagem
        this.x = x;  // Guarda a coordenada X onde a personagem nasce
        this.y = y;  // Guarda a coordenada Y onde a personagem nasce

        // Define as dimensões da personagem
        this.width = width;  // Define a largura da personagem em pixels
        this.height = height;  // Define a altura da personagem em pixels

        // Configura os atributos de vida
        this.maxHealth = maxHealth;  // Define a vida máxima da personagem
        this.health = maxHealth;  // Inicia com vida completa

        // Configura os atributos de combate
        this.attackDamage = attackDamage;  // Define quanto dano a personagem causa

        // Configura a velocidade de movimento
        this.moveSpeed = moveSpeed;  // Define a velocidade com que a personagem se move

        // Carrega a textura da personagem
        this.texture = new Texture(texturePath);  // Carrega a imagem da personagem do ficheiro

        // Cria a área de colisão
        this.hitbox = new Rectangle(x, y, width, height);  // Define a área para detetar colisões
    }

    /**
     * Aplica dano à personagem.
     * Reduz a vida e verifica se a personagem morreu.
     */
    public void takeDamage(int damage) {  // Método chamado quando a personagem recebe dano
        this.health -= damage;  // Reduz a vida atual pelo valor do dano recebido

        // Garante que a vida não fica com valores negativos
        if (this.health <= 0) {  // Se a vida chegou a zero ou menos
            this.health = 0;  // Define a vida como zero (personagem morta)
        }
    }

    /**
     * Cura a personagem, aumentando a sua vida.
     * Não permite ultrapassar a vida máxima.
     */
    public void heal(int amount) {  // Método para curar a personagem
        this.health += amount;  // Aumenta a vida atual pelo valor de cura

        // Garante que a vida não ultrapassa o máximo permitido
        if (this.health > maxHealth) {  // Se a vida ultrapassou o máximo
            this.health = maxHealth;  // Limita a vida ao valor máximo
        }
    }

    /**
     * Move a personagem para uma nova posição.
     * Atualiza também a área de colisão.
     */
    public void setPosition(float newX, float newY) {  // Método para mover a personagem
        // Atualiza a posição da personagem
        this.x = newX;  // Define a nova coordenada X
        this.y = newY;  // Define a nova coordenada Y

        // Atualiza a área de colisão para a nova posição
        this.hitbox.set(x, y, width, height);  // Move a hitbox para a nova posição
    }

    /**
     * Desenha a personagem no ecrã.
     */
    public void render(SpriteBatch batch) {  // Método chamado para desenhar a personagem
        batch.draw(texture, x, y, width, height);  // Desenha a textura da personagem na sua posição atual
    }

    /**
     * Método abstrato para atualizar a personagem.
     * Cada subclasse deve implementar o seu próprio comportamento de atualização.
     */
    public abstract void update(float deltaTime);  // Método que deve ser implementado pelas subclasses

    /**
     * Método abstrato para verificar se a personagem pode atacar.
     * Cada subclasse define as suas próprias regras de ataque.
     */
    public abstract boolean canAttack();  // Método que deve ser implementado pelas subclasses

    // === MÉTODOS GETTER PARA ACEDER AOS ATRIBUTOS ===

    /**
     * Devolve a posição X atual da personagem.
     */
    public float getX() {  // Método para obter a coordenada X
        return x;  // Devolve a posição horizontal atual
    }

    /**
     * Devolve a posição Y atual da personagem.
     */
    public float getY() {  // Método para obter a coordenada Y
        return y;  // Devolve a posição vertical atual
    }

    /**
     * Devolve a largura da personagem.
     */
    public float getWidth() {  // Método para obter a largura
        return width;  // Devolve a largura em pixels
    }

    /**
     * Devolve a altura da personagem.
     */
    public float getHeight() {  // Método para obter a altura
        return height;  // Devolve a altura em pixels
    }

    /**
     * Devolve a vida atual da personagem.
     */
    public int getHealth() {  // Método para obter a vida atual
        return health;  // Devolve quantos pontos de vida a personagem tem
    }

    /**
     * Devolve a vida máxima da personagem.
     */
    public int getMaxHealth() {  // Método para obter a vida máxima
        return maxHealth;  // Devolve o máximo de vida que a personagem pode ter
    }

    /**
     * Devolve o dano de ataque da personagem.
     */
    public int getAttackDamage() {  // Método para obter o dano de ataque
        return attackDamage;  // Devolve quantos pontos de dano a personagem causa
    }

    /**
     * Devolve a velocidade de movimento da personagem.
     */
    public float getMoveSpeed() {  // Método para obter a velocidade
        return moveSpeed;  // Devolve a velocidade de movimento em pixels por segundo
    }

    /**
     * Devolve a área de colisão da personagem.
     */
    public Rectangle getHitbox() {  // Método para obter a hitbox
        return hitbox;  // Devolve o retângulo usado para detetar colisões
    }

    /**
     * Verifica se a personagem está morta.
     */
    public boolean isDead() {  // Método para verificar se a personagem morreu
        return health <= 0;  // Devolve verdadeiro se a vida for zero ou menos
    }

    /**
     * Liberta os recursos gráficos usados pela personagem.
     * Deve ser chamado quando a personagem já não é necessária.
     */
    public void dispose() {  // Método para libertar recursos da memória
        if (texture != null) {  // Se existe uma textura carregada
            texture.dispose();  // Liberta a memória usada pela textura
        }
    }
    /**
     * Define a nova coordenada X da personagem.
     */
    public void setX(float x) {
        this.x = x;
        this.hitbox.set(this.x, this.y, this.width, this.height);  // Atualiza a hitbox
    }

    /**
     * Define a nova coordenada Y da personagem.
     */
    public void setY(float y) {
        this.y = y;
        this.hitbox.set(this.x, this.y, this.width, this.height);  // Atualiza a hitbox
    }

}
