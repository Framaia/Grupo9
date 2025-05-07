package dungeon.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

// Implementação da classe Player usando o padrão Entity-Component
public class Player {
    // Componente de position
    private float x, y;
    private float width = 128f;
    private float height = 128f;
    private float moveSpeed = 300f;
    private Rectangle hitbox;

    // Componente de visuals
    private Texture texture;

    // Componente de stats
    private int health;
    private int maxHealth;
    private int attackDamage;
    private float attackCooldown = 0.5f;
    private float lastAttackTime = 0f;

    // Componente de inventory
    private List<Item> inventory;
    private int keys = 0;
    private int gold = 0;

    // Componente de room transition
    private boolean isInRoomTransition = false;
    private Room.DoorPosition exitDirection;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("player.png");
        this.health = 100;
        this.maxHealth = 100;
        this.attackDamage = 10;
        this.inventory = new ArrayList<>();
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public void update(float deltaTime) {
        lastAttackTime += deltaTime;

        // Implementação do movimento com WASD ou setas
        handleInput(deltaTime);

        // Atualiza a hitbox
        hitbox.set(x, y, width, height);
    }

    private void handleInput(float deltaTime) {
        // Movimento para a esquerda
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= moveSpeed * deltaTime;
        }

        // Movimento para a direita
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += moveSpeed * deltaTime;
        }

        // Movimento para cima
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += moveSpeed * deltaTime;
        }

        // Movimento para baixo
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= moveSpeed * deltaTime;
        }

        // Limitar os movimentos do jogador para dentro da tela
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - width));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - height));
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public boolean canAttack() {
        return lastAttackTime >= attackCooldown;
    }

    public void attack() {
        lastAttackTime = 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public void increaseDamage(int amount) {
        attackDamage += amount;
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public void addKey() {
        keys++;
    }

    public boolean hasKey() {
        return keys > 0;
    }

    public void useKey() {
        if (keys > 0) {
            keys--;
        }
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void setRoomTransition(boolean isInTransition, Room.DoorPosition direction) {
        this.isInRoomTransition = isInTransition;
        this.exitDirection = direction;
    }

    public boolean isInRoomTransition() {
        return isInRoomTransition;
    }

    public Room.DoorPosition getExitDirection() {
        return exitDirection;
    }

    public void resetRoomTransition() {
        isInRoomTransition = false;
        exitDirection = null;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public int getGold() {
        return gold;
    }

    public int getKeys() {
        return keys;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void dispose() {
        texture.dispose();
    }
}
