package dungeon.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Item {
    // Tipos de itens
    public enum ItemType {
        HEALTH_POTION("health_potion.png", "Poção de Vida", 20, "Restaura 20 pontos de vida"),
        DAMAGE_BOOST("damage_boost.png", "Amuleto de Força", 5, "Aumenta seu dano em 5 pontos"),
        KEY("key.png", "Chave", 0, "Abre portas trancadas"),
        GOLD_COIN("gold_coin.png", "Moeda de Ouro", 10, "10 moedas de ouro");

        private final String texturePath;
        private final String name;
        private final int value;
        private final String description;

        ItemType(String texturePath, String name, int value, String description) {
            this.texturePath = texturePath;
            this.name = name;
            this.value = value;
            this.description = description;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    // Componente de position
    private float x, y;
    private float width = 32f;
    private float height = 32f;
    private Rectangle hitbox;

    // Componente de visuals
    private Texture texture;

    // Componente de item properties
    private ItemType type;
    private boolean isCollected;

    // Componente de animation properties
    private float bobHeight = 10f;
    private float bobSpeed = 2f;
    private float bobTimer = 0f;
    private float originalY;

    public Item(float x, float y, ItemType type) {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.type = type;
        this.texture = new Texture(type.getTexturePath());
        this.isCollected = false;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public void update(float deltaTime) {
        if (!isCollected) {
            // Animação simples de flutuação
            bobTimer += deltaTime;
            y = originalY + (float) Math.sin(bobTimer * bobSpeed) * bobHeight;

            // Atualiza a hitbox
            hitbox.set(x, y, width, height);
        }
    }

    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public boolean checkCollision(Player player) {
        if (isCollected) {
            return false;
        }

        return hitbox.overlaps(player.getHitbox());
    }

    public void collect() {
        isCollected = true;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public ItemType getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void dispose() {
        texture.dispose();
    }
}
