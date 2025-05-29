package dungeon.core;  // Define o pacote onde a classe está inserida

import com.badlogic.gdx.Gdx;  // Acesso ao sistema de entrada (teclado)
import com.badlogic.gdx.Input;  // Leitura de teclas pressionadas
import com.badlogic.gdx.graphics.Texture;  // Carregamento de texturas
import com.badlogic.gdx.graphics.g2d.SpriteBatch;  // Para desenhar texturas no ecrã
import dungeon.core.player.PlayerCombatComponent;  // Componente que gere o combate
import dungeon.core.player.PlayerAnimationHandler;  // Componente que gere a animação (sprite)
import java.util.ArrayList;  // Estrutura de dados para o inventário
import java.util.List;  // Interface de lista genérica

// Classe que representa o jogador controlado pelo utilizador
// Herda de Character e implementa funcionalidades específicas do Player
public class Player extends Character {

    // Controla o tempo entre ataques
    private float attackCooldown = 0.5f;  // Tempo mínimo entre ataques
    private float lastAttackTime = 0f;    // Tempo decorrido desde o último ataque

    // Componente de combate — trata da lógica ofensiva
    private final PlayerCombatComponent combat;

    // Componente de animação — troca o sprite conforme o estado (com ou sem espada)
    private final PlayerAnimationHandler animationHandler;

    // Inventário do jogador
    private List<Item> inventory;  // Lista de itens
    private int keys = 0;          // Número de chaves
    private int gold = 0;          // Quantidade de ouro

    // Transição entre salas
    private boolean isInRoomTransition = false;  // Indica se está a atravessar uma porta
    private Room.DoorPosition exitDirection;     // Direção da saída usada

    /**
     * Construtor do jogador. Inicializa todos os atributos e componentes.
     * @param x Posição X inicial
     * @param y Posição Y inicial
     */
    public Player(float x, float y) {
        super(x, y, 64, 64, 100, 10, 150f, "player.png");  // Chama o construtor da classe Character

        this.inventory = new ArrayList<>();  // Cria o inventário vazio
        this.combat = new PlayerCombatComponent(this);  // Inicializa o sistema de combate

        // Carrega as texturas (normal e com espada)
        Texture normal = new Texture("player.png");
        Texture combat = new Texture("player_combat.png");

        // Inicializa o sistema de animação, que troca entre as duas imagens
        this.animationHandler = new PlayerAnimationHandler(this, normal, combat);
    }

    /**
     * Atualiza o estado do jogador a cada frame.
     * @param deltaTime Tempo decorrido desde o último frame
     */
    @Override
    public void update(float deltaTime) {
        lastAttackTime += deltaTime;          // Atualiza o tempo desde o último ataque
        combat.update(deltaTime);             // Atualiza o estado de combate
        hitbox.set(x, y, width, height);      // Atualiza a hitbox para a nova posição
    }

    /**
     * Desenha o jogador no ecrã.
     * @param batch SpriteBatch usado para desenhar
     */
    @Override
    public void render(SpriteBatch batch) {
        animationHandler.render(batch);  // Desenha o sprite correto (normal ou em combate)
    }

    /**
     * Verifica se o jogador pode atacar.
     * @return true se o tempo de espera já passou
     */
    @Override
    public boolean canAttack() {
        return lastAttackTime >= attackCooldown;
    }

    /**
     * Reinicia o tempo de espera após atacar.
     */
    public void attack() {
        lastAttackTime = 0;
    }

    /**
     * Aumenta o dano de ataque do jogador.
     * @param amount Valor a adicionar ao dano
     */
    public void increaseDamage(int amount) {
        attackDamage += amount;
    }

    /**
     * Adiciona um item ao inventário.
     * @param item Item a adicionar
     */
    public void addToInventory(Item item) {
        inventory.add(item);
    }

    /**
     * Adiciona uma chave ao inventário.
     */
    public void addKey() {
        keys++;
    }

    /**
     * Verifica se o jogador tem pelo menos uma chave.
     * @return true se tiver chaves
     */
    public boolean hasKey() {
        return keys > 0;
    }

    /**
     * Usa uma chave, se houver disponível.
     */
    public void useKey() {
        if (keys > 0) keys--;
    }

    /**
     * Adiciona ouro ao inventário.
     * @param amount Quantidade a adicionar
     */
    public void addGold(int amount) {
        gold += amount;
    }

    /**
     * Define o estado de transição entre salas.
     */
    public void setRoomTransition(boolean isInTransition, Room.DoorPosition direction) {
        this.isInRoomTransition = isInTransition;
        this.exitDirection = direction;
    }

    /**
     * Verifica se o jogador está a atravessar uma sala.
     */
    public boolean isInRoomTransition() {
        return isInRoomTransition;
    }

    /**
     * Obtém a direção da porta de saída.
     */
    public Room.DoorPosition getExitDirection() {
        return exitDirection;
    }

    /**
     * Limpa o estado de transição entre salas.
     */
    public void resetRoomTransition() {
        isInRoomTransition = false;
        exitDirection = null;
    }

    /**
     * Fornece acesso ao sistema de combate do jogador.
     * @return o componente de combate
     */
    public PlayerCombatComponent getCombat() {
        return this.combat;
    }

    /**
     * Fornece acesso ao inventário do jogador.
     */
    public List<Item> getInventory() {
        return inventory;
    }

    /**
     * Obtém a quantidade de ouro do jogador.
     */
    public int getGold() {
        return gold;
    }

    /**
     * Obtém o número de chaves que o jogador tem.
     */
    public int getKeys() {
        return keys;
    }

    /**
     * Devolve a velocidade de movimento do jogador.
     * Necessário para o PlayerController.
     */
    public float getSpeed() {
        return moveSpeed;
    }
}
